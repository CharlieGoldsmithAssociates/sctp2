/*
 * Copyright (C) 2021 CGA Technologies, a trading name of Charlie Goldsmith Associates Ltd
 *  All rights reserved, released under the BSD-3 licence.
 *
 * CGA Technologies develop and use this software as part of its work
 *  but the software itself is open-source software; you can redistribute it and/or modify
 *  it under the terms of the BSD licence below
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS
 *  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 * For more information please see http://opensource.org/licenses/BSD-3-Clause
 */

package org.cga.sctp.security;


import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.security.permission.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessControlService extends TransactionalService {

    @Autowired
    private UserRoleRepository roleRepository;

    @Autowired
    private UserPermissionRepository permissionRepository;

    @Autowired
    private PermissionGroupRepository groupRepository;

    @Autowired
    private UserRolePermissionRepository userRolePermissionRepository;

    public List<UserPermission> getRolePermissions(UserRole role) {
        return permissionRepository.findByRoleName(role.getName());
    }

    public List<PermissionGroup> getPermissionGroups() {
        return groupRepository.findAll();
    }

    public List<UserPermission> getPermissionsByGroup(PermissionGroup group) {
        return permissionRepository.findByGroup(group.getName());
    }

    public PermissionGroup findPermissionGroupById(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    public List<UserRole> getUserRoles() {
        return roleRepository.findAll();
    }

    public UserRole getUserRoleById(Long roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    public List<UserPermissionInfo> getRolePermissionInfo(UserRole userRole) {
        return permissionRepository.getRolePermissionInfo(userRole.getName());
    }

    public UserRole addUserRole(String roleName, boolean active) {
        UserRole role = new UserRole();
        role.setCreatedAt(LocalDateTime.now());
        role.setActive(active);
        role.setDescription(roleName);
        role.setName(format("R%X", System.currentTimeMillis()));
        role.setSystemRole(false);
        roleRepository.save(role);
        return role;
    }

    public void saveUserRole(UserRole userRole) {
        roleRepository.save(userRole);
    }

    public List<RolePermissionDetails> getAvailableRolePermissions(UserRole userRole) {
        return permissionRepository.getAvailableRolePermissions(userRole.getName());
    }

    public void saveUserRolePermissions(List<UserRolePermission> userRolePermissions) {
        userRolePermissionRepository.saveAll(userRolePermissions);
    }

    public UserPermission findPermissionById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    public boolean checkRolePermission(UserRole userRole, UserPermission permission) {
        return userRolePermissionRepository.existsByRoleAndPermission(userRole.getName(), permission.getName());
    }

    /**
     * This method checks if the given permission has parent permissions, then returns the parent permissions that
     * are not already assigned to the given role.
     *
     * @param permission Permission whose parent to retrieve.
     * @param userRole   The role to check against
     * @return Parent permissions if not already assigned to given role or null if the given permission does not
     * have a parent permission or the parent permission is already assigned to the role.
     */
    public List<UserPermission> getParentPermissionsForRoleAssignment(UserPermission permission, UserRole userRole) {
        return permissionRepository.getParentPermissionsForRoleAssignment(userRole.getName(), permission.getName());
    }

    /**
     * <p>Removes the given permission from the given role.</p>
     * <p>Child permissions will also be removed if this permission is a parent.
     * If the permission has a parent, the parent will be removed if:</p>
     * <ol>
     *     <li>Parent permission does not have any other child permissions under the given role</li>
     * </ol>
     *
     * @param permission Permission to remove
     * @param userRole   Role to remove the permission from
     */
    public void removeRolePermission(UserPermission permission, UserRole userRole) {
        userRolePermissionRepository.removeRolePermission(userRole.getName(), permission.getName());
    }

    public List<RolePermissionDetails> getRolePermissionsForDisplay(UserRole userRole) {
        return permissionRepository.getRolePermissionsForDisplay(userRole.getName());
    }
}
