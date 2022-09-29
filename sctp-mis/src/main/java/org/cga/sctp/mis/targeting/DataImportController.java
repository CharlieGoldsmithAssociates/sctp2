/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cga.sctp.mis.targeting;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.navigation.BreadcrumbDefinition;
import org.cga.sctp.mis.core.navigation.BreadcrumbPath;
import org.cga.sctp.mis.core.navigation.ModuleNames;
import org.cga.sctp.mis.core.navigation.VarBinding;
import org.cga.sctp.targeting.exchange.*;
import org.cga.sctp.targeting.importation.ImportTaskService;
import org.cga.sctp.user.*;
import org.cga.sctp.validation.SortFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/data-import")
@Secured({RoleConstants.ROLE_STANDARD, RoleConstants.ROLE_ADMINISTRATOR})
@BreadcrumbDefinition(module = ModuleNames.DATA_IMPORT, index = @BreadcrumbPath(link = "/data-import", title = "Import data from sources", navigable = true))
public class DataImportController extends BaseController {

    private static final HouseholdImportStat EMPTY_HOUSEHOLD_STATS = new HouseholdImportStat() {
        @Override
        public Long getArchived() {
            return 0L;
        }

        @Override
        public Long getNoHead() {
            return 0L;
        }

        @Override
        public Long getDataImportId() {
            return 0L;
        }
    };

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private ImportTaskService importTaskService;
    /**
     * Directory to use to store temporary files for the exports
     */
    @Value("${imports.staging}")
    private String stagingDirectory;

    @GetMapping
    ModelAndView list(@AuthenticatedUserDetails AuthenticatedUser user, Pageable pageable) {
        Page<DataImportView> imports = dataImportService.getDataImports(pageable);
        return view("targeting/import/list")
                .addObject("imports", imports);
    }

    @GetMapping("/{import-id}/details")
    @AdminAndStandardAccessOnly
    @BreadcrumbPath(link = "/{import-id}/details", title = "Data import details", bindings = {@VarBinding(variable = "import-id", lookupKey = "import-id")})
    ModelAndView details(@PathVariable("import-id") Long id, RedirectAttributes attributes) {
        DataImportView importView = dataImportService.findDataImportViewById(id);
        if (importView == null) {
            setDangerFlashMessage("Data import session does not exist.", attributes);
            return redirect("/data-import");
        }
        return view("targeting/import/details")
                .addObject("importStatuses", DataImportObject.ImportStatus.IMPORT_STATUSES)
                .addObject("import", importView);
    }

    private ModelAndView redirectToReview(long id) {
        return redirect(format("/data-import/from-ubr-api/%d/review", id));
    }

    private DataImportView getImportInReviewStatus(Long id, DataImportObject.ImportSource importSource, RedirectAttributes attributes) {
        DataImportView dataImport = dataImportService.findImportViewByIdAndStatus(id, importSource, DataImportObject.ImportStatus.Review);
        if (dataImport == null) {
            setDangerFlashMessage("Cannot find this session import.", attributes);
        }
        return dataImport;
    }

    @PostMapping("/{import-id}/{import-source}/merge")
    @AdminAccessOnly
    ResponseEntity<?> merge(
            @PathVariable("import-id") Long id,
            @PathVariable("import-source") DataImportObject.ImportSource importSource,
            RedirectAttributes attributes) throws ExecutionException, InterruptedException {

        DataImportView dataImport = getImportInReviewStatus(id, importSource, attributes);

        if (dataImport == null) {
            return ResponseEntity.notFound().build();
        }

        return switch (dataImportService.queueDataImportMerge(dataImport)) {
            case Queued -> ResponseEntity.ok().build();
            case InvalidSessionState -> ResponseEntity.badRequest().build();
        };
    }

    @GetMapping("/export-errors/{import-id}")
    @AdminAndStandardAccessOnly
    ResponseEntity<?> exportErrors(@PathVariable("import-id") Long id, RedirectAttributes attributes) {
        Optional<Path> filePath = dataImportService.exportDataImportErrors(id, importTaskService, stagingDirectory);
        // TODO Ideally what should happen here is to queue the process in the background and store a file hash
        //  in a database table, with the user id who initiated the download, where then, the user who
        //  initiated the download can then download the file. So this  method would not have to hang.
        //  Also, a new entity would have to be created which only selects the fields
        //  needed, and as well as streaming the results to the excel file instead of holding the contents in memory.
        if (filePath.isEmpty()) {
            setDangerFlashMessage("Data import session does not exist or export directory not found.", attributes);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(200)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", format("filename=data-import-%s-errors.xlsx", id))
                .body(new FileSystemResource(filePath.get()));
    }

    @GetMapping(value = "/{import-id}/household-imports", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    public ResponseEntity<List<HouseholdImport>> getHouseholdImports(
            @PathVariable("import-id") Long importId,
            @Valid @Min(1) @RequestParam("page") int page,
            @Valid @Min(10) @Max(100) @RequestParam(value = "size", defaultValue = "50", required = false) int size,
            @Valid @RequestParam(value = "order", required = false, defaultValue = "ASC") Sort.Direction sortDirection,
            @Valid @SortFields({"formNumber", "mlCode", "errorCount", "memberCount", "archived", "hasHouseholdHead"})
            @RequestParam(value = "sort", required = false, defaultValue = "formNumber") String sort,
            @RequestParam(value = "slice", required = false, defaultValue = "false") boolean useSlice) {
        DataImport dataImport = dataImportService.findDataImportById(importId);
        if (dataImport == null) {
            return ResponseEntity.notFound().build();
        }

        long pages = 0;
        long total = 0;

        page = page - 1;

        //Pageable pageable = PageRequest.
/*
        final Slice<HouseholdImport> imports =
                useSlice ? dataImportService.getHouseholdImportsSlice(importId, PageRequest.of(page, size, sortDirection, sort))
                        : dataImportService.getHouseholdImportsPage(importId, PageRequest.of(page, size, sortDirection, sort));*/

        final List<HouseholdImport> imports = dataImportService
                .getHouseholdImports(dataImport.getId(), page * size, size, sort, sortDirection);

        if (!useSlice) {
            total = dataImportService.countHouseholdImports(dataImport.getId());
            pages = (total / size) + (total % size > 0 ? 1 : 0);
        }

        return ResponseEntity.ok()
                .header("X-Is-Slice", Boolean.toString(useSlice))
                .header("X-Data-Total", Long.toString(total))
                .header("X-Data-Pages", Long.toString(pages))
                .header("X-Data-Size", Integer.toString(imports.size()))
                .header("X-Data-Page", Integer.toString(page + 1))
                .body(imports);
    }


    @PostMapping(value = "/{import-id}/archive-household",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    @AdminAndStandardAccessOnly
    public ResponseEntity<HouseholdImport> archiveHousehold(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @PathVariable("import-id") Long importId,
            @RequestParam("household-id") Long householdId,
            @RequestParam("archive") Boolean archive) {
        final DataImport dataImport = dataImportService.findDataImportById(importId);
        if (dataImport == null) {
            return ResponseEntity.notFound().build();
        }

        if (dataImport.getStatus() != DataImportObject.ImportStatus.Review) {
            return ResponseEntity.badRequest().build();
        }

        final HouseholdImport householdImport = dataImportService.getHouseholdImport(householdId, dataImport.getId());

        if (householdImport == null) {
            return ResponseEntity.notFound().build();
        }

        dataImportService.archiveHousehold(householdId, importId, archive);

        publishGeneralEvent("%s archived household %d from import %S",
                user.username(), householdId, dataImport.getTitle());

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{import-id}/household-members", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    public ResponseEntity<List<HouseholdMemberImport>> getHouseholdMemberImports(
            @PathVariable("import-id") Long importId,
            @RequestParam("household-id") Long householdId) {
        final DataImport dataImport = dataImportService.findDataImportById(importId);
        if (dataImport == null) {
            return ResponseEntity.notFound().build();
        }

        if (dataImport.getStatus() != DataImportObject.ImportStatus.Review) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(dataImportService.getHouseholdMemberImports(householdId, importId));
    }

    @PostMapping(value = "/{import-id}/update-household-head",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    @AdminAndStandardAccessOnly
    public ResponseEntity<?> updateHouseholdHead(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @PathVariable("import-id") Long importId,
            @RequestParam("household-id") Long householdId,
            @RequestParam("member-id") Long memberId) {
        final DataImport dataImport = dataImportService.findDataImportById(importId);
        if (dataImport == null) {
            return ResponseEntity.notFound().build();
        }

        if (dataImport.getStatus() != DataImportObject.ImportStatus.Review) {
            return ResponseEntity.badRequest().build();
        }

        final HouseholdImport householdImport = dataImportService.getHouseholdImport(householdId, dataImport.getId());

        if (householdImport == null) {
            return ResponseEntity.notFound().build();
        }

        /*if (householdImport.getHasHouseholdHead() && StringUtils.hasText(householdImport.getHouseholdHeadName())) {
            // already has a defined household head
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }*/

        dataImportService.updateHouseholdHead(householdId, importId, memberId);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{import-id}/household-stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    public ResponseEntity<HouseholdImportStat> getHouseholdImportStats(
            @PathVariable("import-id") Long importId) {
        final DataImport dataImport = dataImportService.findDataImportById(importId);
        if (dataImport == null) {
            return ResponseEntity.notFound().build();
        }
        final HouseholdImportStat stat = dataImportService.getHouseholdImportStat(importId);
        return ResponseEntity.ok(stat == null ? EMPTY_HOUSEHOLD_STATS : stat);
    }
}
