/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, CGATechnologies
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

import org.cga.sctp.location.LocationCode;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.mis.core.navigation.BreadcrumbDefinition;
import org.cga.sctp.mis.core.navigation.BreadcrumbPath;
import org.cga.sctp.mis.core.navigation.ModuleNames;
import org.cga.sctp.mis.core.navigation.VarBinding;
import org.cga.sctp.mis.targeting.import_tasks.UBRImportService;
import org.cga.sctp.targeting.exchange.DataImport;
import org.cga.sctp.targeting.exchange.DataImportObject;
import org.cga.sctp.targeting.exchange.DataImportView;
import org.cga.sctp.targeting.importation.UbrHouseholdImport;
import org.cga.sctp.user.AdminAccessOnly;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.cga.sctp.mis.location.LocationCodeUtil.toSelectOptions;

@Controller
@RequestMapping("/data-import/from-ubr-api")
@BreadcrumbDefinition(module = ModuleNames.DATA_IMPORT, index = @BreadcrumbPath(link = "/data-import/from-ubr-api", title = "UBR API import results", navigable = true))
public class UbrApiImportController extends SecuredBaseController {
    @Autowired
    private LocationService locationService;

    @Autowired
    private UBRImportService ubrImportService;

    @GetMapping
    @AdminAndStandardAccessOnly
    ModelAndView viewInitiatePage(@AuthenticatedUserDetails AuthenticatedUser user,
                                  RedirectAttributes attributes) {

        List<LocationCode> districts = locationService.getActiveDistrictCodes();
        return view("targeting/import/ubr/api")
                .addObject("districts", toSelectOptions(districts))
                .addObject("traditionalAuthorities", emptyList())
                .addObject("groupVillageHeads", emptyList());
    }

    @PostMapping
    @AdminAccessOnly
    ModelAndView initiateImportFromAPI(@AuthenticatedUserDetails AuthenticatedUser user,
                                       @Valid @ModelAttribute UbrApiImportDataForm form,
                                       BindingResult result,
                                       RedirectAttributes attributes) {
        if (result.hasErrors()) {
            setDangerFlashMessage("Cannot import data, the form has errors please correct them.", attributes);
            return view("targeting/import/ubr/api");
        }
        LOG.info("UBR Import village clusters {}", form.getGroupVillageHeadCode());

        DataImport dataImport = ubrImportService.queueImportFromUBRAPI(form, user.id());

        return view(redirectWithSuccessMessage(String.format("/data-import/%s/details", dataImport.getId()), "System has initiated Data Import from UBR API. Please check status of the import after a few moments", attributes));
    }

    private DataImportView getImport(Long id, RedirectAttributes attributes) {
        DataImportView dataImport = ubrImportService.getDataImportService().findImportViewByIdAndStatus(id,
                DataImportObject.ImportSource.UBR_API, DataImportObject.ImportStatus.Review);
        if (dataImport == null) {
            setDangerFlashMessage("Cannot find this session import.", attributes);
        }
        return dataImport;
    }

    @GetMapping("/{import-id}/review")
    @AdminAndStandardAccessOnly
    @BreadcrumbPath(link = "/{import-id}/review", title = "Review data", bindings = {@VarBinding(variable = "import-id", lookupKey = "import-id")})
    ModelAndView index(@PathVariable("import-id") Long id, RedirectAttributes attributes, Pageable pageable) {
        DataImportView dataImport = getImport(id, attributes);
        if (dataImport == null) {
            return redirect("/data-import");
        }
        Page<UbrHouseholdImport> imports = ubrImportService.getImportTaskService().getImportsBySessionIdForReview(dataImport.getId(), pageable);
        return view("targeting/import/review")
                .addObject("importSession", dataImport)
                .addObject("imports", imports);
    }
}
