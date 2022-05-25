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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.targeting.exchange.DataImportObject;
import org.cga.sctp.targeting.exchange.DataImportService;
import org.cga.sctp.targeting.exchange.DataImportView;
import org.cga.sctp.targeting.importation.ImportTaskService;
import org.cga.sctp.targeting.importation.UbrHouseholdImport;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.cga.sctp.user.RoleConstants;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/data-import")
@Secured({RoleConstants.ROLE_STANDARD, RoleConstants.ROLE_ADMINISTRATOR})
public class DataImportController extends BaseController {

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
    ModelAndView list(@AuthenticatedUserDetails AuthenticatedUser user) {
        List<DataImportView> imports = dataImportService.getDataImports();
        return view("targeting/import/list")
                .addObject("imports", imports);
    }

    @GetMapping("/{import-id}/details")
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

    private DataImportView getImport(Long id, DataImportObject.ImportSource importSource, RedirectAttributes attributes) {
        DataImportView dataImport = dataImportService.findImportViewByIdAndStatus(id, importSource, DataImportObject.ImportStatus.Review);
        if (dataImport == null) {
            setDangerFlashMessage("Cannot find this session import.", attributes);
        }
        return dataImport;
    }

    @PostMapping("/{import-id}/{import-source}/merge")
    ModelAndView merge(
            @AuthenticationPrincipal String username,
            @Valid @ModelAttribute MergeImportsForm form,
            BindingResult bindingResult,
            @PathVariable("import-id") Long id,
            @PathVariable("import-source") DataImportObject.ImportSource importSource,
            RedirectAttributes attributes) {
        DataImportView dataImport = getImport(id, importSource, attributes);
        if (dataImport == null) {
            return redirect("/data-import");
        }
        if (bindingResult.hasErrors()) {
            setDangerFlashMessage("Cannot merge data at this moment.", attributes);
            return redirectToReview(id);
        }
        try {
            dataImportService.mergeBatchIntoPopulation(dataImport);
            setSuccessFlashMessage("Data imported successfully", attributes);
            publishGeneralEvent("%s merged data in population from import session %s.", username, dataImport.getTitle());
            return redirect("/data-import");
        } catch (Exception e) {
            setDangerFlashMessage("There was an error when importing the data.", attributes);
            LOG.error("Exception during merge", e);
        }

        return redirectToReview(id);
    }

    @GetMapping("/export-errors/{import-id}")
    ResponseEntity<?> exportErrors(@PathVariable("import-id") Long id, RedirectAttributes attributes) {
        Optional<Path> filePath = dataImportService.exportDataImportErrors(id, importTaskService, stagingDirectory);
        try {
            if (filePath.isEmpty()) {
                setDangerFlashMessage("Data import session does not exist or export directory not found.", attributes);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(200)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", format("filename=data-import-%s-errors.xlsx", id))
                    .body(Files.readAllBytes(filePath.get()));
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to export beneficiaries", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
