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

package org.cga.sctp.mis.transfers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.targeting.enrollment.EnrollmentService;
import org.cga.sctp.transfers.TransferService;
import org.cga.sctp.transfers.parameters.TransferParametersService;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/transfers/sessions")
public class TransferSessionsController extends BaseController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProgramService programService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransferParametersService transferParametersService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserService userService;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView index(Pageable pageable) {
        var transferSessions = transferService.findAllActiveSessions(pageable);
        // TODO: Remember transfer Summaries and transfer sessions are two different concepts
        return view("transfers/index")
                .addObject("transferSessions", transferSessions)
                .addObject("transferSummaries", new Object()); // FIXME: fetch summary
    }

    @GetMapping("/initiate")
    @AdminAndStandardAccessOnly
    public ModelAndView getInitiateTransfersPage(RedirectAttributes attributes) {
        List<Program> programs = programService.getActivePrograms();
        List<Location> districts = locationService.getActiveDistricts();

        if (programs.isEmpty()) {
            return redirectOnFailedCondition("/transfers/home", "Cannot calculate Transfers when there are no Programmes registered", attributes);
        }

        if (districts.isEmpty()) {
            return redirectOnFailedCondition("/transfers/home", "Cannot calculate Transfers when there are no Locations registered", attributes);
        }

        return view("/transfers/initiate")
                .addObject("programs", programs)
                .addObject("districts", districts);
    }
}
