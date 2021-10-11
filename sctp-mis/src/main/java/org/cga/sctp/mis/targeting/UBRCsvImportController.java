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

import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.cga.sctp.mis.file_upload.FileUploadService;
import org.cga.sctp.mis.targeting.import_tasks.FileImportService;
import org.cga.sctp.targeting.exchange.DataImport;
import org.cga.sctp.targeting.exchange.DataImportObject;
import org.cga.sctp.targeting.exchange.DataImportView;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.cga.sctp.user.RoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/data-import/from-ubr-csv")
@Secured({RoleConstants.ROLE_STANDARD, RoleConstants.ROLE_ADMINISTRATOR})
public class UBRCsvImportController extends UBRImportController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private TusFileUploadService tusFileUploadService;

    @GetMapping
    ModelAndView form(@ModelAttribute("form") UbrCsvDataImportForm form) {
        return view("targeting/import/ubr/csv");
    }

    @GetMapping("/household-template-file")
    ResponseEntity<Resource> downloadHouseholdTemplateFile() {
        Resource resource = fileImportService.getUbrHouseholdTemplate();
        return ResponseEntity
                .ok().header("Content-Type", "text/csv")
                .header("Content-Disposition", format("attachment; filename=\"%s\"", resource.getFilename()))
                .body(resource);
    }

    @GetMapping("/{id}/upload-file")
    ModelAndView uploadForm(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @PathVariable("id") Long sessionId,
            RedirectAttributes attributes) {
        DataImportView dataImport = importService.findImportViewByIdAndStatus(sessionId,
                DataImportObject.ImportSource.UBR_CSV,
                DataImportObject.ImportStatus.FileUploadPending);
        if (dataImport == null) {
            setDangerFlashMessage("Import session not found or file upload was already completed", attributes);
            return redirectToImports();
        }
        if (!dataImport.getImporterUserId().equals(user.id())) {
            setDangerFlashMessage(format("To avoid inconsistencies, only %s can upload a file to this session.",
                    dataImport.getImportedBy()), attributes);
            return redirectToImports();
        }
        return view("targeting/import/ubr/csv-upload")
                .addObject("dataImport", dataImport);
    }

    @PostMapping("/delete")
    ModelAndView delete(
            @AuthenticationPrincipal String username,
            @Valid @ModelAttribute DeleteUbrCsvImportForm form,
            BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            setDangerFlashMessage("Cannot delete this import session at the moment.", attributes);
            return redirectToImports();
        }
        DataImport dataImport = importService.findPendingUbrCsvImport(form.getId());
        if (dataImport != null) {
            importService.deleteDataImport(dataImport);
            setSuccessFlashMessage("Data import session deleted.", attributes);
            publishGeneralEvent("%s deleted import session %s.", username, dataImport);
        } else {
            setDangerFlashMessage("Cannot delete this import session at the moment.", attributes);
        }
        return redirectToImports();
    }

    @PostMapping
    ModelAndView create(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute("form") UbrCsvDataImportForm form,
            BindingResult result, RedirectAttributes attributes) {

        DataImport dataImport;

        if (result.hasErrors()) {
            return view("targeting/import/new");
        }

        dataImport = new DataImport();
        dataImport.setIndividuals(0L);
        dataImport.setHouseholds(0L);
        dataImport.setBatchDuplicates(0L);
        dataImport.setPopulationDuplicates(0L);
        dataImport.setTitle(form.getTitle());
        dataImport.setImporterUserId(user.id());
        dataImport.setDataSource(DataImportObject.ImportSource.UBR_CSV);
        dataImport.setImportDate(LocalDateTime.now());
        dataImport.setStatus(DataImportObject.ImportStatus.FileUploadPending);
        dataImport.setStatusText(DataImportObject.ImportStatus.FileUploadPending.title);

        importService.saveDataImport(dataImport);

        publishGeneralEvent("%s created import session %s.",
                user.username(), form.getTitle());

        setSuccessFlashMessage("Import session created. Now it is time to upload the file. You can always come back to this page to upload your file.", attributes);

        return redirect(format("/data-import/from-ubr-csv/%d/upload-file", dataImport.getId()));
    }

    @RequestMapping(
            value = {"/{session-id}/upload", "/{session-id}/upload/**"},
            method = {RequestMethod.POST, RequestMethod.PATCH,
                    RequestMethod.HEAD, RequestMethod.DELETE,
                    RequestMethod.OPTIONS, RequestMethod.GET}
    )
    public void processUpload(
            @PathVariable("session-id") Long sessionId,
            @AuthenticatedUserDetails AuthenticatedUser user,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) throws IOException {

        UploadInfo uploadInfo;
        DataImport dataImport = importService.findImportByIdAndStatus(sessionId,
                DataImportObject.ImportSource.UBR_CSV,
                DataImportObject.ImportStatus.FileUploadPending);

        if (dataImport == null || !dataImport.getImporterUserId().equals(user.id())) {
            servletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // process file
        tusFileUploadService.process(servletRequest, servletResponse, user.username());

        try {
            uploadInfo = tusFileUploadService.getUploadInfo(servletRequest.getRequestURI(), user.username());
            if (uploadInfo != null) {
                if (!uploadInfo.isUploadInProgress()) {
                    try (InputStream is = tusFileUploadService.getUploadedBytes(servletRequest.getRequestURI(), user.username())) {
                        if (fileUploadService.moveToStagingDirectory(is, uploadInfo)) {
                            // Update status to processing
                            dataImport.setStatus(DataImportObject.ImportStatus.Processing);
                            dataImport.setStatusText("Queued for import by task");
                            dataImport.setSourceFile(uploadInfo.getId().toString());
                            importService.saveDataImport(dataImport);
                            fileImportService.queueImport(dataImport);
                        }
                    }

                    // upload is finished
                    // move to staging directory
                    // Update to processing
                    // delete from temp
                    tusFileUploadService.deleteUpload(servletRequest.getRequestURI(), user.username());
                }
            }
        } catch (TusException | IOException e) {
            LOG.error("Error getting upload information", e);
            servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
