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

package org.cga.sctp.mis.role;


import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.security.AccessControlService;
import org.cga.sctp.security.RolePermissionDetails;
import org.cga.sctp.security.UserPermissionInfo;
import org.cga.sctp.security.permission.UserPermission;
import org.cga.sctp.security.permission.UserRole;
import org.cga.sctp.security.permission.UserRolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
public class RoleController extends BaseController {

    @Autowired
    private AccessControlService accessControlService;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_ROLES')")
    ModelAndView getUserRoles() {
        List<UserRole> roles = accessControlService.getUserRoles();
        return view("/roles/roles")
                .addObject("roles", roles);
    }

    @GetMapping("/{role-id}")
    @PreAuthorize("hasAuthority('READ_ROLES')")
    ModelAndView getRolePermissions(@PathVariable(value = "role-id") Long roleId, RedirectAttributes attributes) {
        UserRole userRole = accessControlService.getUserRoleById(roleId);
        if (userRole == null) {
            setDangerFlashMessage("Selected user role does not exist.", attributes);
            return redirect("/roles");
        }
        List<UserPermissionInfo> permissions = accessControlService.getRolePermissionInfo(userRole);
        return view("/roles/permissions")
                .addObject("role", userRole)
                .addObject("permissions", permissions);
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('WRITE_ROLES')")
    ModelAndView getNewRoleForm(AddRoleForm addRoleForm) {
        return view("/roles/new")
                .addObject("options", Booleans.VALUES)
                .addObject(addRoleForm);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_ROLES')")
    ModelAndView addNewRole(
            @AuthenticationPrincipal String username,
            @Validated @ModelAttribute AddRoleForm addRoleForm,
            BindingResult result, RedirectAttributes attributes) {
        UserRole userRole;
        if (result.hasErrors()) {
            return view("/roles/new")
                    .addObject("options", Booleans.VALUES)
                    .addObject(addRoleForm);
        }
        userRole = accessControlService.addUserRole(addRoleForm.getRoleName(), addRoleForm.getActive().value);

        publishGeneralEvent(format("%s added a new role %s.", username, addRoleForm.getRoleName()));

        setSuccessFlashMessage("Role added successfully. Now assign some permissions to the role.", attributes);
        return redirect(format("/roles/%d/permissions", userRole.getId()));
    }

    @GetMapping("/{role-id}/edit")
    @PreAuthorize("hasAuthority('WRITE_ROLES')")
    ModelAndView getUpdateForm(
            @PathVariable("role-id") long roleId,
            @ModelAttribute RoleUpdateForm roleUpdateForm,
            RedirectAttributes attributes) {
        UserRole userRole = accessControlService.getUserRoleById(roleId);
        if (userRole == null) {
            setDangerFlashMessage("Selected role does not exist.", attributes);
            return redirect("/roles");
        }
        if (userRole.isSystemRole()) {
            setDangerFlashMessage("Selected role cannot be edited.", attributes);
            return redirect("/roles");
        }
        roleUpdateForm.setId(userRole.getId());
        roleUpdateForm.setActive(Booleans.of(userRole.isActive()));
        roleUpdateForm.setRoleName(userRole.getDescription());
        return view("/roles/edit")
                .addObject("options", Booleans.VALUES)
                .addObject(roleUpdateForm);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('WRITE_ROLES')")
    ModelAndView getUpdateForm(
            @AuthenticationPrincipal String username,
            @Validated @ModelAttribute RoleUpdateForm roleUpdateForm,
            BindingResult result,
            RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return view("/roles/edit")
                    .addObject("options", Booleans.VALUES)
                    .addObject(roleUpdateForm);
        }
        UserRole userRole = accessControlService.getUserRoleById(roleUpdateForm.getId());

        if (userRole == null) {
            setDangerFlashMessage("Selected role does not exist.", attributes);
            return redirect("/roles");
        }

        if (userRole.isSystemRole()) {
            setDangerFlashMessage("Selected role cannot be edited.", attributes);
            return redirect("/roles");
        }

        publishGeneralEvent(format("%s updated a role. Old name=%s, new name %s.",
                username,
                userRole.getDescription(), roleUpdateForm.getRoleName()));

        userRole.setDescription(roleUpdateForm.getRoleName());
        userRole.setActive(roleUpdateForm.getActive().value);

        accessControlService.saveUserRole(userRole);

        setSuccessFlashMessage("Role updated.", attributes);
        return redirect("/roles");
    }

    @GetMapping("/{role-id}/permissions")
    @PreAuthorize("hasAuthority('WRITE_ROLES')")
    ModelAndView getPermissionsForm(
            @PathVariable("role-id") long roleId,
            RedirectAttributes attributes) {
        UserRole userRole = accessControlService.getUserRoleById(roleId);
        if (userRole == null) {
            setDangerFlashMessage("Selected role does not exist.", attributes);
            return redirect("/roles");
        }
        if (userRole.isSystemRole()) {
            setDangerFlashMessage("Selected role cannot be edited.", attributes);
            return redirect("/roles");
        }
        return view("/roles/permission-manager")
                .addObject("role", userRole)
                .addObject("availablePermissions", flatten(accessControlService.getAvailableRolePermissions(userRole)))
                .addObject("rolePermissions", flatten(accessControlService.getRolePermissionsForDisplay(userRole)));
    }

    @PostMapping("/add-role-permission")
    @PreAuthorize("hasAuthority('WRITE_ROLES')")
    ModelAndView addRolePermission(
            @Validated @ModelAttribute RolePermissionForm permissionsForm,
            BindingResult result,
            RedirectAttributes attributes) {

        UserRole userRole;
        UserPermission permission;
        List<UserPermission> parentPermissions;
        List<UserRolePermission> userRolePermissions;

        if (result.hasErrors()) {
            setDangerFlashMessage("There was an error processing that request.", attributes);
            return redirect("/roles");
        }

        userRole = accessControlService.getUserRoleById(permissionsForm.getRoleId());

        if (userRole == null) {
            setDangerFlashMessage("Selected role does not exist.", attributes);
            return redirect("/roles");
        }
        if (userRole.isSystemRole()) {
            setDangerFlashMessage("Selected role cannot be edited.", attributes);
            return redirect("/roles");
        }

        permission = accessControlService.findPermissionById(permissionsForm.getPermissionId());
        if (permission == null) {
            setDangerFlashMessage("Selected permission does not exist.", attributes);
            return redirect(format("/roles/%d/permissions", userRole.getId()));
        }
        if (permission.isSystemPermission() || !permission.isActive()) {
            setDangerFlashMessage("Selected permission may not be assigned to any role.", attributes);
            return redirect(format("/roles/%d/permissions", userRole.getId()));
        }

        if (accessControlService.checkRolePermission(userRole, permission)) {
            setDangerFlashMessage("This permission is already assigned to this role.", attributes);
            return redirect(format("/roles/%d/permissions", userRole.getId()));
        }

        userRolePermissions = new LinkedList<>();
        // The parent permission in this case is an implicit permission.
        parentPermissions = accessControlService.getParentPermissionsForRoleAssignment(permission, userRole);
        if (!parentPermissions.isEmpty()) {
            for (UserPermission parent : parentPermissions) {
                userRolePermissions.add(newUserRolePermission(userRole, parent));
            }
        }
        userRolePermissions.add(newUserRolePermission(userRole, permission));

        accessControlService.saveUserRolePermissions(userRolePermissions);

        setSuccessFlashMessage("Permission successfully added to role" +
                ".", attributes);
        return redirect(format("/roles/%d/permissions", userRole.getId()));
    }

    @PostMapping("/remove-role-permission")
    @PreAuthorize("hasAuthority('WRITE_ROLES')")
    ModelAndView removeRolePermission(
            @Validated @ModelAttribute RolePermissionForm permissionsForm,
            BindingResult result,
            RedirectAttributes attributes) {

        UserRole userRole;
        UserPermission permission;
        List<UserPermission> parentPermissions;
        List<UserRolePermission> userRolePermissions;

        if (result.hasErrors()) {
            setDangerFlashMessage("There was an error processing that request.", attributes);
            return redirect("/roles");
        }

        userRole = accessControlService.getUserRoleById(permissionsForm.getRoleId());

        if (userRole == null) {
            setDangerFlashMessage("Selected role does not exist.", attributes);
            return redirect("/roles");
        }
        if (userRole.isSystemRole()) {
            setDangerFlashMessage("Selected role cannot be edited.", attributes);
            return redirect("/roles");
        }

        permission = accessControlService.findPermissionById(permissionsForm.getPermissionId());
        if (permission == null) {
            setDangerFlashMessage("Selected permission does not exist.", attributes);
            return redirect(format("/roles/%d/permissions", userRole.getId()));
        }
        if (permission.isSystemPermission() || !permission.isActive()) {
            setDangerFlashMessage("Selected permission cannot be removed from this role.", attributes);
            return redirect(format("/roles/%d/permissions", userRole.getId()));
        }

        if (!accessControlService.checkRolePermission(userRole, permission)) {
            setDangerFlashMessage("This permission is not assigned to this role.", attributes);
            return redirect(format("/roles/%d/permissions", userRole.getId()));
        }

        accessControlService.removeRolePermission(permission, userRole);

        setSuccessFlashMessage("Permission successfully removed from role.", attributes);
        return redirect(format("/roles/%d/permissions", userRole.getId()));
    }

    private UserRolePermission newUserRolePermission(UserRole role, UserPermission permission) {
        UserRolePermission urp = new UserRolePermission();
        urp.setRole(role.getName());
        urp.setPermission(permission.getName());
        return urp;
    }

    // flatten and combine the permissions into a single dimension collection
    private List<PermissionTableItem> flatten(List<RolePermissionDetails> permissions) {
        // Use array list to maintain order
        List<PermissionTableItem> items = new ArrayList<>();
        Set<Long> headers = permissions.stream()
                .map(RolePermissionDetails::getGroupId)
                .collect(Collectors.toSet());
        for (long groupId : headers) {
            RolePermissionDetails permission = permissions.stream()
                    .filter(p -> p.getGroupId() == groupId)
                    .findFirst()
                    .get();
            items.add(new PermissionTableItem(null, permission.getGroupName(), true));
            items.addAll(
                    permissions.stream()
                            .filter(availableRolePermission -> availableRolePermission.getGroupId() == groupId)
                            .map(p -> new PermissionTableItem(p.getPermissionId(), p.getPermissionName(), false))
                            .collect(Collectors.toList()));
        }
        return items;
    }
}