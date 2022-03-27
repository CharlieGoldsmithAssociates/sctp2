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

package org.cga.sctp.mis.user;

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.location.LocationType;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.mis.core.templating.SelectOptionItem;
import org.cga.sctp.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@AdminAccessOnly
@Controller
@RequestMapping("/district-users")
public class DistrictUsersController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @GetMapping
    ModelAndView index() {
        return view("users/district/list")
                .addObject("users", userService.getDistrictUsers());
    }

    @PostMapping("/remove")
    ModelAndView removeProfile(
            HttpServletRequest request,
            @AuthenticatedUserDetails AuthenticatedUser user,
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        DistrictUserProfilesView profile = userService.getDistrictUserProfileView(id);
        if (profile != null) {
            userService.deleteDistrictUserProfileById(profile.getId());
            publishEvent(UserAuditEvent.removedFromDistrictProfiles(user.username(), profile, request.getRemoteAddr()));
            setSuccessFlashMessage("Profile removed.", redirectAttributes);
        } else {
            setDangerFlashMessage("Profile does not exist.", redirectAttributes);
        }
        return redirect("/district-users");
    }

    private ModelAndView handleActivation(
            String principal,
            Long profileId,
            String ipAddress,
            boolean activate,
            RedirectAttributes redirectAttributes) {
        DistrictUserProfilesView profile = userService.getDistrictUserProfileView(profileId);
        if (profile == null) {
            setDangerFlashMessage("Profile does not exist.", redirectAttributes);
        } else {
            if (activate) {
                if (!profile.getActive()) {
                    userService.setDistrictUserProfileActive(profile.getId(), true);
                    publishEvent(UserAuditEvent.districtProfileActivated(principal, profile, ipAddress));
                }
                setSuccessFlashMessage("Profile activated", redirectAttributes);
            } else {
                if (profile.getActive()) {
                    userService.setDistrictUserProfileActive(profile.getId(), false);
                    publishEvent(UserAuditEvent.districtProfileDeactivated(principal, profile, ipAddress));
                }
                setSuccessFlashMessage("Profile deactivated", redirectAttributes);
            }
        }
        return redirect("/district-users");
    }

    @PostMapping("/deactivate")
    ModelAndView deactivateProfile(
            HttpServletRequest request,
            @AuthenticatedUserDetails AuthenticatedUser user,
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        return handleActivation(user.username(), id, request.getRemoteAddr(), false, redirectAttributes);
    }

    @PostMapping("/activate")
    ModelAndView activateProfile(
            HttpServletRequest request,
            @AuthenticatedUserDetails AuthenticatedUser user,
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        return handleActivation(user.username(), id, request.getRemoteAddr(), true, redirectAttributes);
    }

    @GetMapping("/new")
    ModelAndView getForm(@ModelAttribute("form") NewDistrictUserForm form) {
        return newForm();
    }

    @GetMapping("/edit")
    ModelAndView getEditForm(
            @RequestParam("id") Long profileId,
            @ModelAttribute("form") EditDistrictUserForm form,
            RedirectAttributes redirectAttributes) {
        DistrictUserProfilesView profile = userService.getDistrictUserProfileView(profileId);
        if (profile == null) {
            setDangerFlashMessage("Selected profile does not exist", redirectAttributes);
            return redirect("/district-users");
        }
        form.setDistrictId(profile.getDistrictId());
        return editForm(profile);
    }

    private ModelAndView editForm(DistrictUserProfilesView profile) {
        List<Location> districts = locationService.getActiveDistricts();
        return view("/users/district/edit")
                .addObject("profile", profile)
                .addObject("districts", districts);
    }

    private ModelAndView newForm() {
        List<Location> districts = locationService.getActiveDistricts();
        List<SelectOptionItem> users = userService.getDistrictUserProspects()
                .stream()
                .map(prospect -> new SelectOptionItem(prospect.getId(), prospect.getFullName()))
                .collect(Collectors.toList());
        return view("/users/district/new")
                .addObject("booleans", Booleans.VALUES)
                .addObject("users", users)
                .addObject("districts", districts);
    }

    private boolean isQualifiedUser(User user) {
        return user != null && !user.isSystemUser() && user.isActive() && !user.isDeleted()
                && (user.getRole() == SystemRole.ROLE_ADMINISTRATOR || user.getRole() == SystemRole.ROLE_STANDARD);
    }

    @PostMapping("/add")
    ModelAndView addDistrictUser(
            HttpServletRequest request,
            @AuthenticatedUserDetails AuthenticatedUser principal,
            @Valid @ModelAttribute("form") NewDistrictUserForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return newForm();
        }
        User user = userService.findById(form.getUserId());
        if (!isQualifiedUser(user)) {
            return setDangerMessage(newForm(), "Cannot add selected user to district");
        }
        Location location = locationService.findActiveLocationById(form.getDistrictId());
        if (location == null || location.getLocationType() != LocationType.SUBNATIONAL1) {
            return setDangerMessage(newForm(), "Selected district cannot be used.");
        }
        DistrictUserProfile profile = new DistrictUserProfile();
        profile.setDistrictId(location.getId());
        profile.setUserId(user.getId());
        profile.setCreatedAt(Instant.now());
        profile.setActive(form.getActive());

        userService.saveDistrictUserProfile(profile);

        publishEvent(
                UserAuditEvent.districtProfileAdded(
                        principal.username(),
                        user,
                        location,
                        request.getRemoteAddr()
                )
        );

        setSuccessFlashMessage("District user profile added", redirectAttributes);
        return redirect("/district-users");
    }

    @PostMapping("/update")
    ModelAndView updateProfile(
            HttpServletRequest request,
            @AuthenticatedUserDetails AuthenticatedUser principal,
            @RequestParam("profile") Long profileId,
            @Valid @ModelAttribute("form") EditDistrictUserForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        DistrictUserProfilesView profile = userService.getDistrictUserProfileView(profileId);
        if (profile == null) {
            setDangerFlashMessage("Profile does not exist", redirectAttributes);
            return redirect("/district-users");
        }

        if (bindingResult.hasErrors()) {
            return editForm(profile);
        }
        Long oldDistrict = profile.getDistrictId();
        Location location = locationService.findActiveLocationById(form.getDistrictId());
        if (location == null || location.getLocationType() != LocationType.SUBNATIONAL1) {
            bindingResult.addError(new FieldError("form", "districtId", "Invalid location selected."));
            return editForm(profile);
        }

        if (!oldDistrict.equals(location.getId())) {
            profile.setDistrictId(location.getId());
            userService.setDistrictUserProfileDistrict(profile, location);

            publishEvent(
                    UserAuditEvent.districtProfileDistrictChanged(
                            principal.username(),
                            profile,
                            location,
                            request.getRemoteAddr()
                    )
            );
        }
        setSuccessFlashMessage("District user profile updated", redirectAttributes);
        return redirect("/district-users");
    }
}
