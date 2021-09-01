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

package org.cga.sctp.mis.terminologies;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.terminology.LocationTerminology;
import org.cga.sctp.terminology.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/terminologies")
public class TerminologyController extends BaseController {
    @Autowired
    private TerminologyService terminologyService;

    @GetMapping
    ModelAndView index(@ModelAttribute("terminologyForm") TerminologyForm form) {
        LocationTerminology terminology = terminologyService.getLocationTerminology();

        form.setSubnational1(terminology.getSubnational1());
        form.setSubnational2(terminology.getSubnational2());
        form.setSubnational3(terminology.getSubnational3());
        form.setSubnational4(terminology.getSubnational4());
        form.setSubnational5(terminology.getSubnational5());

        return view("terminologies/edit")
                .addObject("terminology", terminology);
    }

    @PostMapping("/update")
    ModelAndView updateTerminology(
            @AuthenticationPrincipal String username,
            HttpServletRequest request,
            @Validated @ModelAttribute("terminologyForm") TerminologyForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return view("terminologies/edit")
                    .addObject("terminology", terminologyService.getLocationTerminology());
        }

        LocationTerminology terminology = new LocationTerminologyImpl(form);

        terminologyService.updateLocationTerminology(terminology);

        publishGeneralEvent("%s updated location terminologies. SRC: %s.", username, request.getRemoteAddr());

        setSuccessFlashMessage("Terminologies updated", attributes);
        return redirect("/terminologies");
    }
}
