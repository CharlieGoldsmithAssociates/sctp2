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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationCode;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.targeting.exchange.DataImport;
import org.cga.sctp.targeting.exchange.DataImportObject;
import org.cga.sctp.targeting.exchange.DataImportService;
import org.cga.sctp.targeting.exchange.DataImportView;
import org.cga.sctp.targeting.importation.ImportTaskService;
import org.cga.sctp.targeting.importation.UbrApiDataToHouseholdImportMapper;
import org.cga.sctp.targeting.importation.UbrHouseholdImport;
import org.cga.sctp.targeting.importation.ubrapi.UbrApiClient;
import org.cga.sctp.targeting.importation.ubrapi.UbrRequest;
import org.cga.sctp.targeting.importation.ubrapi.data.UbrApiDataResponse;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.cga.sctp.mis.location.LocationCodeUtil.toSelectOptions;

@Controller
@RequestMapping("/data-import/from-ubr-api")
public class UbrApiImportController extends BaseController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private ImportTaskService importTaskService;

    @Autowired
    private UbrApiClient ubrApiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    ModelAndView viewInitiatePage(@AuthenticatedUserDetails AuthenticatedUser user,
                                  RedirectAttributes attributes) {

        List<LocationCode> districts = locationService.getActiveDistrictCodes();
        return view("targeting/import/ubr/api")
                .addObject("districts", toSelectOptions(districts))
                .addObject("traditionalAuthorities", emptyList())
                .addObject("groupVillageHeads", emptyList());
    }

    @PostMapping
    ModelAndView initiateImportFromAPI(@AuthenticatedUserDetails AuthenticatedUser user,
                                       @Valid @ModelAttribute UbrApiImportDataForm form,
                                       BindingResult bindingResult,
                                       RedirectAttributes attributes) throws JsonProcessingException {

        form.setProgrammes(UbrRequest.UBR_SCTP_PROGRAMME_CODE);
        form.setLowerPercentileCategory(0L);
        form.setUpperPercentileCategory(100L);

        DataImport dataImport = new DataImport();
        dataImport.setTitle(form.getTitle());
        dataImport.setDataSource(DataImportObject.ImportSource.UBR_API);
        dataImport.setImporterUserId(user.id());
        dataImport.setCompletionDate(null);
        dataImport.setBatchDuplicates(0L);
        dataImport.setHouseholds(0L);
        dataImport.setIndividuals(0L);
        dataImport.setNewHouseholds(0L);
        dataImport.setOldHouseholds(0L);
        dataImport.setPopulationDuplicates(0L);
        String requestJson = objectMapper.writeValueAsString(form);
        dataImport.setSourceFile(requestJson);
        dataImport.setStatus(DataImportObject.ImportStatus.FileUploadPending);
        dataImport.setStatusText("Waiting for API Data Fetch");
        dataImport.setImportDate(LocalDateTime.now());

        dataImportService.saveDataImport(dataImport);


        publishGeneralEvent("User:%s initiated Import from UBR API with parameters: %s", user.username(), requestJson);

        dataImport.setStatus(DataImportObject.ImportStatus.Processing);

        // TODO: initiate the data import in another thread via the import service
        UbrApiDataResponse response = ubrApiClient.fetchExistingHouseholds(form);
        if (response == null) {
            return view(redirectWithDangerMessage("/data-import", "Failed to import data from UBR API", attributes));
        }

        UbrApiDataToHouseholdImportMapper mapper = new UbrApiDataToHouseholdImportMapper();

        importTaskService.saveImports(mapper.mapFromApiData(dataImport, response));

        dataImport.setStatus(DataImportObject.ImportStatus.Review);

        dataImportService.closeImportSession(dataImport);

        publishGeneralEvent("Data Import Complete from UBR API with parameters: %s, initiated by User:%s", requestJson, user.username());

        return redirect(String.format("/data-import/%s/details", dataImport.getId()));
    }

    private DataImportView getImport(Long id, RedirectAttributes attributes) {
        DataImportView dataImport = dataImportService.findImportViewByIdAndStatus(id,
                DataImportObject.ImportSource.UBR_API, DataImportObject.ImportStatus.Review);
        if (dataImport == null) {
            setDangerFlashMessage("Cannot find this session import.", attributes);
        }
        return dataImport;
    }

    @GetMapping("/{import-id}/review")
    ModelAndView index(@PathVariable("import-id") Long id, RedirectAttributes attributes, Pageable pageable) {
        DataImportView dataImport = getImport(id, attributes);
        if (dataImport == null) {
            return redirect("/data-import");
        }
        List<UbrHouseholdImport> imports = importTaskService.getImportsBySessionIdForReview(dataImport.getId(), pageable);
        return view("targeting/import/review")
                .addObject("importSession", dataImport)
                .addObject("imports", imports);
    }
}
