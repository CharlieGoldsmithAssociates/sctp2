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

package org.cga.sctp.mis.account;

import org.cga.sctp.auth.AuthService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.User;
import org.cga.sctp.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
public class AccountController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping("/password")
    public ModelAndView getPasswordForm(@ModelAttribute PasswordForm passwordForm) {
        return view("/account/password").addObject(passwordForm);
    }

    @PostMapping("/password")
    public ModelAndView updatePassword(Authentication authentication, @Validated @ModelAttribute PasswordForm passwordForm, BindingResult result) {
        AuthenticatedUser userDetails = getAuthenticationDetails(authentication);
        User user;

        if (result.hasErrors()) {
            return view("/account/password")
                    .addObject(result)
                    .addObject(passwordForm);
        }

        if (passwordForm.getOldPassword().equals(passwordForm.getNewPassword())) {
            return withDangerMessage("/account/password", "New password must be different from old password.");
        }

        if ((user = userService.findById(userDetails.id())) == null) {
            return withDangerMessage("/account/password", "You cannot update your password at the moment.");
        }

        if (!authService.verifyPassword(passwordForm.getOldPassword(), user.getPassword())) {
            return withDangerMessage("/account/password", "Invalid password entered.");
        }

        user.setAuthAttempts(0);
        user.setPassword(authService.hashPassword(passwordForm.getNewPassword()));
        userService.saveUser(user);

        return withSuccessMessage("/account/password", "Password successfully updated!");
    }

    @GetMapping("/settings")
    public ModelAndView getSettingsForm(Authentication authentication, @ModelAttribute("settingsForm") UserSettingsForm settingsForm) {
        AuthenticatedUser details = getAuthenticationDetails(authentication);
        User user = userService.findById(details.id());

        settingsForm.setEmail(user.getEmail());
        settingsForm.setFirstName(user.getFirstName());
        settingsForm.setLastName(user.getLastName());

        return view("/account/settings")
                .addObject(settingsForm);
    }

    @PostMapping("/settings")
    public String processSettingsForm(
            Authentication authentication,
            Model model,
            @Validated @ModelAttribute("settingsForm") UserSettingsForm settingsForm,
            BindingResult result,
            RedirectAttributes attributes) {
        AuthenticatedUser details;
        User user;

        if (result.hasErrors()) {
            model.addAttribute(settingsForm);
            return "account/settings";
        }

        details = getAuthenticationDetails(authentication);

        user = userService.findById(details.id());

        // Has email address changed?
        if (!settingsForm.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userService.emailExists(settingsForm.getEmail())) {
                setDangerMessage(model, "The email address that you entered is already in use.");
                return "account/settings";
            }
            user.setEmail(settingsForm.getEmail());
        }

        user.setFirstName(settingsForm.getFirstName());
        user.setLastName(settingsForm.getLastName());

        userService.saveUser(user);

        // update authenticated details in current session
        ((UsernamePasswordAuthenticationToken) authentication).setDetails(new AuthenticatedUser(user));

        return redirectWithSuccessMessage("/account/settings", "Information updated successfully", attributes);
    }
}
