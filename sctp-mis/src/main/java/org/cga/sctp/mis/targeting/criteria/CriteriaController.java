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

package org.cga.sctp.mis.targeting.criteria;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.targeting.TargetingService;
import org.cga.sctp.targeting.criteria.Criterion;
import org.cga.sctp.targeting.criteria.CriterionView;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.cga.sctp.user.RoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/criteria")
public class CriteriaController extends BaseController {

    @Autowired
    private TargetingService targetingService;

    @GetMapping
    @Secured({RoleConstants.ROLE_ADMINISTRATOR, RoleConstants.ROLE_STANDARD})
    public ModelAndView list() {
        List<CriterionView> criteria = targetingService.getTargetingCriteria();
        return view("targeting/criteria/index")
                .addObject("criteria", criteria);
    }

    @GetMapping("/new")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView newForm(@ModelAttribute("form") CriterionForm form) {
        return view("targeting/criteria/new")
                .addObject("booleans", Booleans.VALUES);
    }

    @GetMapping("/edit")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView edit(
            @RequestParam("id") Long id,
            @ModelAttribute("form") CriterionUpdateForm form, RedirectAttributes attributes) {

        Criterion criterion = targetingService.findCriterionById(id);
        if (criterion == null) {
            setDangerFlashMessage("Targeting criterion not found.", attributes);
            return redirect("/criteria");
        }
        form.setId(criterion.getId());
        form.setName(criterion.getName());
        form.setActive(Booleans.of(criterion.getActive()));
        return view("targeting/criteria/edit")
                .addObject("booleans", Booleans.VALUES);
    }

    @PostMapping("/update")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView update(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute("form") CriterionUpdateForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return view("targeting/criteria/edit")
                    .addObject("booleans", Booleans.VALUES);
        }

        Criterion criterion = targetingService.findCriterionById(form.getId());

        if (criterion == null) {
            setDangerFlashMessage("Targeting criterion not found.", attributes);
            return redirect("/criteria");
        }

        criterion.setName(form.getName());
        criterion.setActive(form.getActive().value);

        targetingService.saveTargetingCriterion(criterion);

        publishGeneralEvent("%s updated targeting criterion %d(%s)",
                user.username(), criterion.getId(), form.getName());

        setSuccessFlashMessage("Criterion successfully updated", attributes);

        return redirect("/criteria");
    }

    @PostMapping
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView create(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute("form") CriterionForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return view("targeting/criteria/new")
                    .addObject("booleans", Booleans.VALUES);
        }

        Criterion criterion = new Criterion();
        criterion.setCreatedBy(user.id());
        criterion.setName(form.getName());
        criterion.setActive(form.getActive().value);
        criterion.setCreatedAt(LocalDateTime.now());

        targetingService.saveTargetingCriterion(criterion);

        publishGeneralEvent("%s created targeting criterion %s", user.username(), form.getName());
        setSuccessFlashMessage("Criterion successfully created. Now add some filters", attributes);

        return redirect(format("/criteria/%d/filters", criterion.getId()));
    }
}
