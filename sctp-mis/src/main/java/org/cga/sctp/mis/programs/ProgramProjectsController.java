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

package org.cga.sctp.mis.programs;

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.program.*;
import org.cga.sctp.user.RoleConstants;
import org.cga.sctp.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/programs/{program-id}/projects")
@Secured({RoleConstants.ROLE_ADMINISTRATOR})
public class ProgramProjectsController extends BaseController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private ProgramService programService;

    private Program findProgramById(Long id, RedirectAttributes attributes) {
        Program program = programService.getProgramById(id);
        if (program == null) {
            setDangerFlashMessage("Selected program does not exist.", attributes);
        }
        return program;
    }

    private ModelAndView redirectToPrograms() {
        return redirect("/programs");
    }

    private ModelAndView redirectToProgramProjects(Long programId) {
        return redirect(format("/programs/%d/projects", programId));
    }

    private List<ProgramUserImpl> mapProgramUserInterfaceToPojo(List<ProgramUser> programUsers) {
        return programUsers.stream().map(ProgramUserImpl::new).collect(Collectors.toList());
    }

    private List<ProgramUserCandidateImpl> mapProgramUserCandidateInterfaceToPojo(List<ProgramUserCandidate> candidates) {
        return candidates.stream().map(ProgramUserCandidateImpl::new).collect(Collectors.toList());
    }

    @GetMapping
    public ModelAndView index(@PathVariable("program-id") Long programId, RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        String returnUrl;
        if (program == null) {
            return redirectToPrograms();
        }

        returnUrl = "/programs";

        return view("programs/list")
                .addObject("parent", program)
                .addObject("returnUrl", returnUrl)
                .addObject("programs", programService.getProgramProjects(programId));
    }

    @GetMapping("/new")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    ModelAndView newProject(@PathVariable("program-id") Long programId,
                            @ModelAttribute("programForm") ProgramForm programForm,
                            RedirectAttributes attributes) {

        Program program = findProgramById(programId, attributes);
        String returnUrl;
        if (program == null) {
            return redirectToPrograms();
        }

        returnUrl = format("/programs/%d/projects", programId);

        return view("programs/new")
                .addObject("booleans", Booleans.VALUES)
                .addObject("parent", program)
                .addObject("returnUrl", returnUrl)
                .addObject("locations", locationService.getActiveCountries());
    }

    @PostMapping
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    ModelAndView addProject(
            @PathVariable("program-id") Long programId,
            @AuthenticationPrincipal String username,
            HttpServletRequest request,
            @Validated @ModelAttribute("programForm") ProgramForm programForm,
            BindingResult result,
            RedirectAttributes attributes) {

        Program program;
        Location location;

        Program parentProgram = findProgramById(programId, attributes);

        if (result.hasErrors()) {
            return view("/programs/new")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("locations", locationService.getActiveCountries());
        }

        if ((location = locationService.findById(programForm.getLocation())) == null) {
            return withDangerMessage("/programs/new", "Selected location does not exist")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("locations", locationService.getActiveCountries());
        }

        if (programForm.getEndDate() != null) {
            if (DateUtils.isDateAfter(programForm.getStartDate(), programForm.getEndDate())) {
                return withDangerMessage("/programs/new", "Start date must be before end date.")
                        .addObject("booleans", Booleans.VALUES)
                        .addObject("locations", locationService.getActiveCountries());
            }
        }

        program = new Program();
        program.setLocation(location.getId());
        program.setName(programForm.getName());
        program.setCreatedAt(LocalDateTime.now());
        program.setEndDate(programForm.getEndDate());
        program.setActive(programForm.getActive().value);
        program.setStartDate(programForm.getStartDate());
        program.setCode(format("PG%X", System.currentTimeMillis()));
        program.setParentId(programId);
        program.setProgrammeType(ProgrammeType.PROJECT);


        programService.save(program);

        publishGeneralEvent("%s added new %s project %s.", username, parentProgram.getName(), programForm.getName());

        setSuccessFlashMessage("Project successfully added!", attributes);

        return redirectToProgramProjects(programId);
    }

    @GetMapping("/users")
    public ModelAndView addProjectUser(@PathVariable("program-id") Long programId, RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        if (program == null) {
            return redirectToPrograms();
        }
        return view("/programs/users/list")
                .addObject("program", program)
                .addObject("users", mapProgramUserInterfaceToPojo(programService.getProgramUsers(programId)));
    }

    @GetMapping("/funders")
    @Secured({RoleConstants.ROLE_STANDARD, RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView projectFunders(@PathVariable("program-id") Long programId, RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        if (program == null) {
            return redirectToPrograms();
        }
        return view("/programs/funders/list")
                .addObject("program", program)
                .addObject("funders", programService.getProgramFunders(programId));
    }

    @GetMapping("/funders/edit")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView edit(@PathVariable("program-id") Long programId, RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        if (program == null) {
            return redirectToProgramProjects(programId);
        }
        return view("/programs/funders/edit")
                .addObject("program", program)
                .addObject("available", programService.getAvailableProgramFunders(programId))
                .addObject("funders", programService.getProgramFunders(programId));
    }

}
