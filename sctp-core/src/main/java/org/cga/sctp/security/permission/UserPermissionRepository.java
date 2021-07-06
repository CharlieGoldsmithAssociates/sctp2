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

package org.cga.sctp.security.permission;

import org.cga.sctp.security.RolePermissionDetails;
import org.cga.sctp.security.UserPermissionInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPermissionRepository extends CrudRepository<UserPermission, Long> {

    @Query(nativeQuery = true, value = "CALL getRolePermissions(:role)")
    List<UserPermission> findByRoleName(@Param("role") String roleName);

    /**
     * Find all permissions under the given group name
     *
     * @param group Name of group
     * @return list of permissions
     */
    @Query(nativeQuery = true, value = "select * from permissions where `group` = :group and system_permission = false")
    List<UserPermission> findByGroup(@Param("group") String group);

    @Query(nativeQuery = true, value = "CALL getRolePermissionsInfo(:role)")
    List<UserPermissionInfo> getRolePermissionInfo(@Param("role") String role);

    @Query(nativeQuery = true, value = "CALL getAvailableRolePermissions(:role)")
    List<RolePermissionDetails> getAvailableRolePermissions(@Param("role") String role);

    @Query(nativeQuery = true, value = "CALL getRolePermissionsForDisplay(:role)")
    List<RolePermissionDetails> getRolePermissionsForDisplay(@Param("role") String role);

    @Query(nativeQuery = true, value = "CALL getParentPermissionForRoleAssignment(:role, :passion)")
    List<UserPermission> getParentPermissionsForRoleAssignment(@Param("role") String roleName, @Param("passion") String permissionName);
}
