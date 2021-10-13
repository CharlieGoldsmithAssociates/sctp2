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

package org.cga.sctp.program;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ProgramRepository extends JpaRepository<Program, Long> {

    @Query(nativeQuery = true, value = "select * from program_info_v where programme_type = :type")
    List<ProgramInfo> getProgramInfo(@Param("type") String type);

    @Query(nativeQuery = true, value = "call getProgramFunders(:programId)")
    List<ProgramFunder> getProgramFunders(@Param("programId") Long programId);

    @Query(nativeQuery = true, value = "call getAvailableProgramFunders(:programId)")
    List<ProgramFunder> getAvailableProgramFunders(@Param("programId") Long programId);

    @Modifying
    @Query(nativeQuery = true, value = "delete from program_funders where program_id = :id and funder_id in (:ids)")
    void removeProgramFunders(@Param("id") Long id, @Param("ids") List<Long> funderIds);

    @Modifying
    @Query(nativeQuery = true, value = "call addProgramFunders(:id, :ids)")
    void addProgramFunders(@Param("id") Long id, @Param("ids") String funderIds);

    @Query(nativeQuery = true, value = "call getProgramUsers(:id)")
    List<ProgramUser> getProgramUsers(@Param("id") Long programId);

    @Query(nativeQuery = true, value = "call getProgramUserCandidates(:id)")
    List<ProgramUserCandidate> getProgramUserCandidates(@Param("id") Long id);

    @Query(nativeQuery = true, value = "SELECT EXISTS (SELECT TRUE FROM program_users pu WHERE pu.user_id = :uid AND pu.program_id = :pid)")
    Integer canUserAccessProgram(@Param("uid") Long userId, @Param("pid") Long programId);

    @Modifying
    @Query(nativeQuery = true, value = "call removeProgramUser(:programId, :userId)")
    void removeProgramUser(@Param("programId") Long programId, @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "select * from program_info_v where parent_id = :parent AND programme_type = :type ")
    List<ProgramInfo> getByProgramProjects(@Param("parent") Long parentId, @Param("type") String type);

    List<Program> getByActive(boolean active);

    Program findByActiveAndId(boolean active, Long id);
}
