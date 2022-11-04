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

package org.cga.sctp.mis.transfers.topups;

import org.apache.commons.collections4.map.HashedMap;
import org.cga.sctp.funders.Funder;
import org.cga.sctp.funders.FundersService;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.location.LocationType;
import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.transfers.topups.*;
import org.cga.sctp.user.AdminAccessOnly;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Controller
@RequestMapping("/transfers/topups")
public class TransferTopUpsController extends SecuredBaseController {

    @Autowired
    private ProgramService programService;

    @Autowired
    private FundersService fundersService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TopUpService topUpService;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView getIndex() {
        List<TopUp> topups = topUpService.findAllActive(Pageable.unpaged()); // TODO: get page parameters from request
        return view("transfers/topups/list")
                .addObject("topups", topups);
    }

    @GetMapping("/new")
    @AdminAndStandardAccessOnly
    public ModelAndView getNewPage(RedirectAttributes attributes) {
        List<Program> programs = programService.getActivePrograms();
        List<Location> districts = locationService.getActiveDistricts();
        List<Funder> funders = fundersService.getActiveFunders();

        if (programs.isEmpty()) {
            return redirectOnFailedCondition("/transfers/topups", "Cannot create TopUps when there are no Programmes registered", attributes);
        }

        if (districts.isEmpty()) {
            return redirectOnFailedCondition("/transfers/topups", "Cannot create TopUps when there are no Locations registered", attributes);
        }

        if (funders.isEmpty()) {
            return redirectOnFailedCondition("/transfers/topups", "Cannot create TopUps when there are no Funders registered", attributes);
        }

        // The page is handled by VueJS, we only need to send pageData obj
        Map<String, Object> pageData = new HashedMap<>();
        pageData.put("booleans", Booleans.values());
        pageData.put("topupTypes", TopUpType.values());
        pageData.put("householdStatuses", TopUpHouseholdStatus.values());
        pageData.put("locationTypes", LocationType.values());
        pageData.put("districts", districts);
        pageData.put("programs", programs);
        pageData.put("funders", funders);

        return view("transfers/topups/new")
                .addObject("booleans", Booleans.values())
                .addObject("topupTypes", TopUpType.values())
                .addObject("householdStatuses", TopUpHouseholdStatus.values())
                .addObject("locationTypes", LocationType.values())
                .addObject("districts", districts)
                .addObject("programs", programs)
                .addObject("funders", funders)
                .addObject("pageData", pageData);
    }

    @GetMapping("/new/preview")
    @AdminAndStandardAccessOnly
    public ResponseEntity<Object> getTopupAmountPreview() {
        // this action returns a preview of the topup applied to the given location and households there-in
        return null;
    }

    @PostMapping(value = "/new",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    @AdminAndStandardAccessOnly
    public ResponseEntity<Object> postNewPage(@AuthenticatedUserDetails AuthenticatedUser user,
                                              @RequestBody NewTopUpForm form) {
        try {
            form.setUserId(user.id());
            Optional<TopUp> topUp = topUpService.newTopup(form);
            if (topUp.isPresent()) {
                return ResponseEntity.ok(topUp.get());
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/view/{topup-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getView(@PathVariable("topup-id") final Long topupId, @AuthenticatedUserDetails AuthenticatedUser user, RedirectAttributes attributes) {
        Optional<TopUp> optionalTopUp = topUpService.findById(topupId);
        if (optionalTopUp.isEmpty()) {
            return view(redirectWithDangerMessage("/transfers/topups", "Topup not found", attributes));
        }

        return view("/transfers/topups/view")
                .addObject("topup", optionalTopUp.get());
    }

    @GetMapping("/delete/{topup-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getDelete(@PathVariable("topup-id") final Long topupId) {
        Optional<TopUp> optionalTopUp = topUpService.findById(topupId);
        if (optionalTopUp.isEmpty()) {
            return redirect("/transfers/topups"); // TODO: redirect with flash message?
        }

        return view("/transfers/topups/delete")
                .addObject("topup", optionalTopUp.get());
    }

    @PostMapping("/delete/{topup-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView postDelete(@PathVariable("topup-id") final Long topupId, @AuthenticatedUserDetails AuthenticatedUser user, RedirectAttributes attributes) {
        topUpService.deleteById(topupId);
        publishGeneralEvent("User %s deleted topup with id %s", user.username(), topupId);
        return view(redirectWithSuccessMessage("/transfers/topups", "TopUp deleted successfully", attributes));
    }
}
