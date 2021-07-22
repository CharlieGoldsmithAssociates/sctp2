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

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.program.*;
import org.cga.sctp.user.AccessLevel;
import org.cga.sctp.user.RoleConstants;
import org.cga.sctp.user.User;
import org.cga.sctp.user.UserService;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/programs/{program-id}/users")
@Secured({RoleConstants.ROLE_ADMINISTRATOR})
public class ProgramUsersController extends BaseController {

    @Autowired
    private ProgramService programService;

    @Autowired
    private UserService userService;

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

    private ModelAndView redirectToProgramUsers(Long programId) {
        return redirect(format("/programs/%d/users", programId));
    }

    private List<ProgramUserImpl> mapProgramUserInterfaceToPojo(List<ProgramUser> programUsers) {
        return programUsers.stream().map(ProgramUserImpl::new).collect(Collectors.toList());
    }

    private List<ProgramUserCandidateImpl> mapProgramUserCandidateInterfaceToPojo(List<ProgramUserCandidate> candidates) {
        return candidates.stream().map(ProgramUserCandidateImpl::new).collect(Collectors.toList());
    }

    private boolean canAddUserToProgram(User user) {
        return user != null
                && !user.isSystemUser()
                && user.isActive()
                && !user.isDeleted()
                && !user.getRole().isRestricted;
    }

    @GetMapping
    public ModelAndView index(@PathVariable("program-id") Long programId, RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        if (program == null) {
            return redirectToPrograms();
        }
        return view("/programs/users/list")
                .addObject("program", program)
                .addObject("users", mapProgramUserInterfaceToPojo(programService.getProgramUsers(programId)));
    }

    @PostMapping("/remove")
    public ModelAndView removeUser(
            HttpServletRequest request,
            @AuthenticationPrincipal String username,
            @PathVariable("program-id") Long programId,
            @Validated @ModelAttribute("userForm") RemoveProgramUserForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        Program program = findProgramById(programId, attributes);

        if (program == null) {
            return redirectToPrograms();
        }

        if (result.hasErrors()) {
            setDangerFlashMessage("Could not process your request. Please try again.", attributes);
        } else {
            if (programService.canUserAccessProgram(form.getUserId(), programId)) {
                String removedUser = userService.getUsernameById(form.getUserId());
                programService.removeProgramUser(form.getUserId(), programId);
                publishGeneralEvent("%s removed %s from %s programme from IP %s.",
                        username, removedUser, program.getName(), request.getRemoteAddr()
                );
                setSuccessFlashMessage("User was successfully removed the programme.", attributes);
            } else {
                setDangerFlashMessage("Selected user was not found under this programme.", attributes);
            }
        }
        return redirectToProgramUsers(programId);
    }

    @PostMapping
    public ModelAndView addUser(
            HttpServletRequest request,
            @AuthenticationPrincipal String username,
            @PathVariable("program-id") Long programId,
            @Validated @ModelAttribute("userForm") NewProgramUserForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        User user;
        Program program = findProgramById(programId, attributes);

        if (program == null) {
            return redirectToPrograms();
        }

        if (result.hasErrors()) {
            return view("/programs/users/new")
                    .addObject("program", program)
                    .addObject("accessLevels", AccessLevel.ACCESS_LEVELS)
                    .addObject("users", mapProgramUserCandidateInterfaceToPojo(programService.getProgramUserCandidates(program)));
        }

        if (!canAddUserToProgram(user = userService.findById(form.getUserId()))) {
            return withDangerMessage("/programs/users/new",
                    "Selected user does not exist or cannot be added to the program at this time.")
                    .addObject("program", program)
                    .addObject("accessLevels", AccessLevel.ACCESS_LEVELS)
                    .addObject("users", mapProgramUserCandidateInterfaceToPojo(programService.getProgramUserCandidates(program)));
        }

        if (programService.canUserAccessProgram(user.getId(), program.getId())) {
            return withDangerMessage("/programs/users/new",
                    "Selected user is already part of this program.")
                    .addObject("program", program)
                    .addObject("accessLevels", AccessLevel.ACCESS_LEVELS)
                    .addObject("users", mapProgramUserCandidateInterfaceToPojo(programService.getProgramUserCandidates(program)));
        }

        if (DateUtils.isDateAfter(program.getStartDate(), form.getStartDate())) {
            return withDangerMessage("/programs/users/new",
                    format("Start date must be on or after the day this program started (%s)",
                            DateUtils.formatDateAsIsoString(program.getStartDate()))
            ).addObject("program", program)
                    .addObject("accessLevels", AccessLevel.ACCESS_LEVELS)
                    .addObject("users", mapProgramUserCandidateInterfaceToPojo(programService.getProgramUserCandidates(program)));
        }

        if (DateUtils.isDateAfter(form.getStartDate(), form.getEndDate())) {
            return withDangerMessage("/programs/users/new",
                    "End date must be after start date.")
                    .addObject("program", program)
                    .addObject("accessLevels", AccessLevel.ACCESS_LEVELS)
                    .addObject("users", mapProgramUserCandidateInterfaceToPojo(programService.getProgramUserCandidates(program)));
        }

        programService.addProgramUser(program, form.getUserId(), form.getAccessLevel(), form.getStartDate(), form.getEndDate());

        publishGeneralEvent("%s added %s to %s programme from IP %s",
                username, user.getUsername(), program.getName(), request.getRemoteAddr()
        );

        setSuccessFlashMessage("User added to program successfully!", attributes);
        return redirectToProgramUsers(programId);
    }

    @GetMapping("/new")
    public ModelAndView newProgramUser(
            @PathVariable("program-id") Long programId,
            @ModelAttribute("userForm") NewProgramUserForm form,
            RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        if (program == null) {
            return redirectToPrograms();
        }
        return view("/programs/users/new")
                .addObject("program", program)
                .addObject("accessLevels", AccessLevel.ACCESS_LEVELS)
                .addObject("users", mapProgramUserCandidateInterfaceToPojo(programService.getProgramUserCandidates(program)));
    }
}
