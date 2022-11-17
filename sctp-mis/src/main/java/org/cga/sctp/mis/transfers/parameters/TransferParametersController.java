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

package org.cga.sctp.mis.transfers.parameters;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.transfers.parameters.EducationTransferParameterRepository;
import org.cga.sctp.transfers.parameters.HouseholdTransferParametersRepository;
import org.cga.sctp.transfers.parameters.TransferParameter;
import org.cga.sctp.transfers.parameters.TransferParametersRepository;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.cga.sctp.validation.SortFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transfers/parameters")
public class TransferParametersController extends BaseController {

    @Autowired
    private ProgramService programService;

    @Autowired
    private TransferParametersRepository transferParameterRepository;

    @Autowired
    private EducationTransferParameterRepository educationTransferParameterRepository;

    @Autowired
    private HouseholdTransferParametersRepository householdTransferParametersRepository;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView getParametersList() {
        return view("transfers/parameters/index");
    }

    @GetMapping("list")
    @AdminAndStandardAccessOnly
    ResponseEntity<List<TransferParameter>> getParametersList(
            @Valid @Min(1) @RequestParam("page") int page,
            @Valid @Min(10) @Max(100) @RequestParam(value = "size", defaultValue = "50", required = false) int size,
            @Valid @RequestParam(value = "order", required = false, defaultValue = "ASC") Sort.Direction sortDirection,
            @Valid @SortFields({"id", "title", "usageCount"})
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sortColumn,
            @RequestParam(value = "slice", required = false, defaultValue = "false") boolean useSlice
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortDirection, sortColumn));
        Page<TransferParameter> transferParameterPage = transferParameterRepository.findAll(pageable);

        return ResponseEntity.ok()
                .header("X-Is-Slice", Boolean.toString(useSlice))
                .header("X-Data-Total", Long.toString(transferParameterPage.getTotalElements()))
                .header("X-Data-Pages", Long.toString(transferParameterPage.getTotalPages()))
                .header("X-Data-Size", Integer.toString(transferParameterPage.getSize()))
                .header("X-Data-Page", Integer.toString(transferParameterPage.getNumber() + 1))
                .body(transferParameterPage.getContent());
    }

    @GetMapping("/{parameter-id}")
    @AdminAndStandardAccessOnly
    public ResponseEntity<TransferParameter> getTransferParameter(@PathVariable("parameter-id") Long parameterId) {
        Optional<TransferParameter> transferParameterOptional = transferParameterRepository.findById(parameterId);
        if (transferParameterOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(transferParameterOptional.get());
    }

    @GetMapping("/view/{parameter-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getParametersList(@PathVariable("parameter-id") Long parameterId) {
        Optional<TransferParameter> transferParameterOptional = transferParameterRepository.findById(parameterId);
        if (transferParameterOptional.isEmpty()) {
            return redirect("/transfers/parameters");
        }

        return view("transfers/parameters/view")
                .addObject("transferParameter", transferParameterOptional.get());
    }

    @GetMapping("/new")
    @AdminAndStandardAccessOnly
    public ModelAndView viewNew() {
        List<Program> programs = programService.getActivePrograms();
        return view("transfers/parameters/new")
                .addObject("programs", programs)
                .addObject("booleans", Booleans.VALUES);
    }

    @PostMapping("/new")
    @AdminAndStandardAccessOnly
    public ResponseEntity<TransferParameter> processAdd(@AuthenticatedUserDetails AuthenticatedUser user,
                                   @Validated @RequestBody TransferParameterForm form) {
        TransferParameter transferParameter = new TransferParameter();
        transferParameter.setProgramId(form.getProgramId());
        transferParameter.setTitle(form.getTitle());
        transferParameter.setActive(form.getActive().value);
        transferParameter.setCreatedBy(user.id());
        transferParameter.setCreatedAt(LocalDateTime.now());
        transferParameter.setUpdatedAt(LocalDateTime.now());

        transferParameterRepository.save(transferParameter);
        return ResponseEntity.ok(transferParameter);

    }

    @PutMapping("/{parameter-id}")
    @AdminAndStandardAccessOnly
    public ResponseEntity<TransferParameter> processEdit(@AuthenticatedUserDetails AuthenticatedUser user,
                                                         @PathVariable("parameter-id") Long parameterId,
                                                         @Validated @RequestBody TransferParameterForm form) {
        TransferParameter transferParameter = transferParameterRepository.getById(parameterId);
        transferParameter.setProgramId(form.getProgramId());
        transferParameter.setTitle(form.getTitle());
        transferParameter.setActive(form.getActive().value);
        transferParameter.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(transferParameterRepository.save(transferParameter));
    }

    @GetMapping("/delete/{parameter-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getDeletePage(@PathVariable("parameter-id") Long parameterId) {
        Optional<TransferParameter> transferParameterOptional = transferParameterRepository.findById(parameterId);
        if (transferParameterOptional.isEmpty()) {
            return redirect("/transfers/parameters");
        }

        return view("transfers/parameters/delete")
                .addObject("transferParameter", transferParameterOptional.get())
                .addObject("householdParameters", householdTransferParametersRepository.findByTransferParameterId(parameterId))
                .addObject("educationBonuses", educationTransferParameterRepository.findByTransferParameterId(parameterId));
    }

    @DeleteMapping("/{parameter-id}")
    @AdminAndStandardAccessOnly
    public ResponseEntity<Void> deleteParameter(@AuthenticatedUserDetails AuthenticatedUser user,
                                       @PathVariable("parameter-id") Long parameterId) {
        Optional<TransferParameter> transferParameterOptional = transferParameterRepository.findById(parameterId);
        if (transferParameterOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TransferParameter parameter = transferParameterOptional.get();
        publishGeneralEvent("User %s deleted parameter with id=%s", user.username(), parameter.getId());
        transferParameterRepository.delete(parameter);
        return ResponseEntity.ok().build();
    }

}
