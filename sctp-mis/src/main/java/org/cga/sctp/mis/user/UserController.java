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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/users")
@Secured({"ROLE_SYSTEM_ADMIN", "ROLE_ADMINISTRATOR"})
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
    ModelAndView getIndex() {
        final List<User> userList = userService.getUsers();
        return view("/users/list")
                .addObject("users", userList);
    }

    @GetMapping("/new")
    ModelAndView addUserForm(@ModelAttribute AddUserForm addUserForm) {
        return view("/users/new")
                .addObject("booleans", Booleans.VALUES)
                .addObject("passwordOptions", PasswordOption.VALUES)
                .addObject("roles", SystemRole.ROLES);
    }

    @PostMapping
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

    @PostMapping("/update")
    ModelAndView updateUser(
            HttpServletRequest request,
            @AuthenticationPrincipal String username,
            @Validated @ModelAttribute("editForm") UserEditForm editForm,
            BindingResult result,
            RedirectAttributes attributes) {

        User user;

        if (result.hasErrors()) {
            return view("/users/edit")
                    .addObject(editForm)
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("roles", SystemRole.ROLES);
        }

        if (editForm.getRole().isRestricted) {
            return withDangerMessage("/users/edit", "Selected role cannot be assigned at the moment. Please select another role.")
                    .addObject(editForm)
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("roles", SystemRole.ROLES);
        }

        user = userService.findById(editForm.getId());
        if (user == null || user.isDeleted()) {
            setDangerFlashMessage("Selected user does not exist.", attributes);
            return redirect("/users");
        }
        if (user.getRole().isRestricted || user.isSystemUser()) {
            setDangerFlashMessage("Selected user cannot be edited at the moment.", attributes);
            return redirect("/users");
        }

        // Is email changing?
        if (!user.getEmail().equalsIgnoreCase(editForm.getEmail())) {
            if (userService.emailExists(editForm.getEmail())) {
                return withDangerMessage("/users/edit", "Email address is already in use.")
                        //.addObject(editForm)
                        .addObject("booleans", Booleans.VALUES)
                        .addObject("roles", SystemRole.ROLES);
            }
            user.setEmail(editForm.getEmail());
        }

        user.setRole(editForm.getRole());
        user.setModifiedAt(LocalDateTime.now());
        user.setLastName(editForm.getLastName());
        user.setFirstName(editForm.getFirstName());
        user.setActive(editForm.getActive().value);
        userService.saveUser(user);

        publishEvent(UserAuditEvent.modified(username, user.getUsername(), request.getRemoteAddr()));

        setSuccessFlashMessage("User updated successfully!", attributes);
        return redirect("/users");
    }

    @GetMapping("/{user-id}/edit")
    ModelAndView editUser(@PathVariable("user-id") Long userId, @ModelAttribute("editForm") UserEditForm editForm, RedirectAttributes attributes) {
        User user = userService.findById(userId);
        if (user == null || user.isDeleted()) {
            setDangerFlashMessage("Selected user does not exist.", attributes);
            return redirect("/users");
        }
        if (user.getRole().isRestricted || user.isSystemUser()) {
            setDangerFlashMessage("Selected user cannot be edited at the moment.", attributes);
            return redirect("/users");
        }
        editForm.setId(user.getId());
        editForm.setRole(user.getRole());
        editForm.setEmail(user.getEmail());
        editForm.setLastName(user.getLastName());
        editForm.setFirstName(user.getFirstName());
        editForm.setUsername(user.getUsername());
        editForm.setActive(Booleans.of(user.isActive()));
        return view("/users/edit")
                .addObject("booleans", Booleans.VALUES)
                .addObject("roles", SystemRole.ROLES);
    }

    @GetMapping("/{user-id}/password")
    ModelAndView editPassword(@PathVariable("user-id") Long userId,
                              @ModelAttribute("passwordForm") UserPasswordForm passwordForm,
                              RedirectAttributes attributes) {
        User user = userService.findById(userId);
        if (user == null || user.isDeleted()) {
            setDangerFlashMessage("Selected user does not exist.", attributes);
            return redirect("/users");
        }
        if (user.getRole().isRestricted || user.isSystemUser()) {
            setDangerFlashMessage("Selected user cannot be edited at the moment.", attributes);
            return redirect("/users");
        }
        passwordForm.setId(user.getId());
        return view("/users/password");
    }

    @PostMapping("/update-password")
    ModelAndView updateUserPassword(
            HttpServletRequest request,
            @AuthenticationPrincipal String username,
            @Validated @ModelAttribute UserPasswordForm passwordForm,
            BindingResult result,
            RedirectAttributes attributes) {

        User user;

        if (result.hasErrors()) {
            return view("/users/password")
                    .addObject(passwordForm);
        }

        user = userService.findById(passwordForm.getId());
        if (user == null || user.isDeleted()) {
            setDangerFlashMessage("Selected user does not exist.", attributes);
            return redirect("/users");
        }
        if (user.getRole().isRestricted || user.isSystemUser()) {
            setDangerFlashMessage("Selected user cannot be edited at the moment.", attributes);
            return redirect("/users");
        }

        user.setPassword(authService.hashPassword(passwordForm.getPassword()));
        userService.saveUser(user);

        publishEvent(UserAuditEvent.password(username, user.getUsername(), request.getRemoteAddr()));

        setSuccessFlashMessage("User password changed successfully!", attributes);
        return redirect("/users");
    }
}
