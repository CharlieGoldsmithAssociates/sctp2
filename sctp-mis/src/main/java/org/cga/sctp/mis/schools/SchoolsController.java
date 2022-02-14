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

package org.cga.sctp.mis.schools;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.schools.School;
import org.cga.sctp.schools.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/schools")
public class SchoolsController extends BaseController {

    @Autowired
    private SchoolService schoolsService;

    @GetMapping
    ModelAndView index() {
        return view("schools/list")
                .addObject("schools", schoolsService.getSchools());
    }

    @GetMapping("/new")
    ModelAndView newSchoolsForm(@ModelAttribute SchoolForm schoolForm) {
        return view("schools/new")
                .addObject("options", Booleans.VALUES);
    }

    @GetMapping("/{school-id}/edit")
    ModelAndView editSchool(@PathVariable("school-id") Long schoolId,
                            @ModelAttribute("schoolForm") SchoolForm schoolForm,
                            RedirectAttributes attributes) {
        Optional<School> schoolOptional = schoolsService.findById(schoolId);
        if (schoolOptional.isEmpty()) {
            setDangerFlashMessage("Selected school does not exist.", attributes);
            return redirect("/schools");
        }
        School school = schoolOptional.get();

        schoolForm.setId(school.getId());
        schoolForm.setName(school.getName());
        // schoolForm.setActive(Booleans.of(school.isActive()));
        return view("/schools/edit")
                .addObject("options", Booleans.VALUES);
    }

    @PostMapping("/update")
    ModelAndView updateSchool(
            @AuthenticationPrincipal String username,
            @Validated @ModelAttribute SchoolForm schoolForm,
            BindingResult result,
            RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return view("/schools/new")
                    .addObject("options", Booleans.VALUES);
        }

        Optional<School> schoolOptional = schoolsService.findById(schoolForm.getId());
        if (schoolOptional.isEmpty()) {
            setDangerFlashMessage("Selected school does not exist.", attributes);
            return redirect("/schools");
        }

        School school = schoolOptional.get();
        publishGeneralEvent("%s updated school: Old name=%s, new name %s.",
                username, school.getName(), schoolForm.getName());

        school.setName(schoolForm.getName());
        school.setCreatedAt(LocalDateTime.now());
        // school.setActive(schoolForm.getActive().value);

        schoolsService.save(school);

        setSuccessFlashMessage("School updated successfully!", attributes);
        return redirect("/schools");
    }

    @PostMapping
    ModelAndView addSchool(
            @AuthenticationPrincipal String username,
            @Validated @ModelAttribute SchoolForm schoolForm,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return view("/schools/new")
                    .addObject("options", Booleans.VALUES);
        }

        School school = new School();
        school.setName(schoolForm.getName());
        school.setCreatedAt(LocalDateTime.now());
        // school.setActive(schoolForm.getActive().value);

        publishGeneralEvent("%s added new school %s.", username, schoolForm.getName());

        schoolsService.save(school);

        setSuccessFlashMessage("School added successfully!", attributes);
        return redirect("/schools");
    }
}
