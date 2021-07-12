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

package org.cga.sctp.mis.user;

import org.cga.sctp.auth.AuthService;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.user.SystemRole;
import org.cga.sctp.security.AccessControlService;
import org.cga.sctp.security.permission.UserRole;
import org.cga.sctp.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private AuthService authService;

    @Autowired
    private LocationService locationService;

    @GetMapping
    @Secured({"ROLE_SYSTEM_ADMIN", "ROLE_ADMINISTRATOR"})
    ModelAndView getIndex() {
        final List<User> userList = userService.getUsers();
        return view("/users/list")
                .addObject("users", userList);
    }

    @GetMapping("/new")
    @Secured({"ROLE_SYSTEM_ADMIN", "ROLE_ADMINISTRATOR"})
    ModelAndView addUserForm(@ModelAttribute AddUserForm addUserForm) {
        List<UserRole> userRoles = accessControlService.getActiveUserRoles();
        return view("/users/new")
                .addObject("booleans", Booleans.VALUES)
                .addObject("passwordOptions", PasswordOption.VALUES)
                .addObject("roles", SystemRole.ROLES);
    }

    @PostMapping
    @Secured({"ROLE_SYSTEM_ADMIN", "ROLE_ADMINISTRATOR"})
    ModelAndView addUser(
            HttpServletRequest request,
            @AuthenticationPrincipal String username,
            @Validated @ModelAttribute AddUserForm addUserForm,
            BindingResult result,
            RedirectAttributes attributes) {

        UserNameEmailLookUp lookUp;

        if (result.hasErrors()) {
            return view("/users/new")
                    .addObject(addUserForm)
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("passwordOptions", PasswordOption.VALUES)
                    .addObject("roles", SystemRole.ROLES);
        }

        if (addUserForm.getRole().isRestricted) {
            return withDangerMessage("/users/new", "Selected role cannot be assigned at the moment. Please select another role.")
                    .addObject(addUserForm)
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("passwordOptions", PasswordOption.VALUES)
                    .addObject("roles", SystemRole.ROLES);
        }

        lookUp = userService.lookupUserNameAndEmail(addUserForm.getUsername(), addUserForm.getEmail());

        if (lookUp.getEmailExists() == 1) {
            return withDangerMessage("/users/new", "Email address is already in use.")
                    .addObject(addUserForm)
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("passwordOptions", PasswordOption.VALUES)
                    .addObject("roles", SystemRole.ROLES);
        }

        if (lookUp.getNameExists() == 1) {
            return withDangerMessage("/users/new", "Username is already in use.")
                    .addObject(addUserForm)
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("passwordOptions", PasswordOption.VALUES)
                    .addObject("roles", SystemRole.ROLES);
        }

        User user = new User();
        user.setSystemUser(false);
        user.setEmail(addUserForm.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        user.setLastName(addUserForm.getLastName());
        user.setUserName(addUserForm.getUsername());
        user.setFirstName(addUserForm.getFirstName());
        user.setActive(addUserForm.getActive().value);
        user.setPassword(authService.generatePassword());
        user.setRole(addUserForm.getRole());
        userService.saveUser(user);

        publishEvent(UserAuditEvent.created(username, addUserForm.getUsername(), request.getRemoteAddr()));

        if (addUserForm.getPasswordOption() == PasswordOption.Manual) {
            setSuccessFlashMessage("User added to the system successfully!. Update the user's password.", attributes);
            return redirect(format("/users/%d/password", user.getId()));
        }

        setSuccessFlashMessage("User added to the system successfully!", attributes);
        return redirect("/users");
    }
}
