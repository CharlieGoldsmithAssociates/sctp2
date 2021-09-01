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

import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.user.AccessLevel;
import org.cga.sctp.user.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgramService extends TransactionalService {

    @Autowired
    private ProgramRepository repository;

    @Autowired
    private ProgramUserEntityRepository userEntityRepository;

    public void save(Program program) {
        repository.save(program);
    }

    public Program getProgramById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<ProgramInfo> getProgramInfo() { return repository.getProgramInfo(ProgrammeType.PROGRAMME.name()); }

    public List<ProgramInfo> getProgramProjects(Long programId) { return repository.getByProgramProjects(programId, ProgrammeType.PROJECT.name()); }

    public List<ProgramFunder> getProgramFunders(Long programId) {
        return repository.getProgramFunders(programId);
    }

    public List<ProgramFunder> getAvailableProgramFunders(Long programId) { return repository.getAvailableProgramFunders(programId); }

    public void removeProgramFunders(Program program, List<Long> funderIds) { repository.removeProgramFunders(program.getId(), funderIds); }

    public void addProgramFunders(Program program, List<Long> funderIds) {
        repository.addProgramFunders(
                program.getId(),
                // Convert to comma-separated list.
                // Will be passed as a single argument (quoted string) to the procedure instead of multiple
                // individual arguments
                format("%s", funderIds.stream().map(Object::toString).collect(Collectors.joining(",")))
        );
    }

    public List<ProgramUser> getProgramUsers(Long programId) {
        return repository.getProgramUsers(programId);
    }

    public List<ProgramUserCandidate> getProgramUserCandidates(Program program) {
        return repository.getProgramUserCandidates(program.getId());
    }

    public boolean canUserAccessProgram(Long userId, Long programId) {
        return repository.canUserAccessProgram(userId, programId) == 1;
    }

    public void addProgramUser(Program program, Long userId, AccessLevel accessLevel, Permission permission, LocalDate startDate, LocalDate endDate) {
        ProgramUserEntity entity = new ProgramUserEntity();
        entity.setEndDate(endDate);
        entity.setStartDate(startDate);
        entity.setAccessLevel(accessLevel);
        entity.setPermission(permission);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setId(new ProgramUserEntity.ProgramUserId(userId, program.getId()));
        userEntityRepository.save(entity);
    }

    public void removeProgramUser(Long userId, Long programId) {
        repository.removeProgramUser(programId, userId);
    }
}
