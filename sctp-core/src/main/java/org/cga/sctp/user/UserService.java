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

package org.cga.sctp.user;

import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.location.Location;
import org.cga.sctp.persistence.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends TransactionalService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DistrictUserProfileRepository districtUserProfileRepository;

    @Autowired
    private DistrictUserProfilesViewRepository districtUserProfilesViewRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User findByUserNameIgnoringStatus(String username) {
        return userRepository.findByUserNameEx(username);
    }

    public User findByUserNameAndSessionId(String userName, String sessionId) {
        return userRepository.findByUserNameAndSessionId(userName, sessionId);
    }

    public boolean emailExists(String email) {
        return userRepository.existsUserByEmail(email);
    }

    public boolean userNameExists(String userName) {
        return userRepository.existsUserByUserName(userName);
    }

    public User findById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getUsers() {
        return userRepository.getNotByStatus(StatusCode.DELETED);
    }

    public UserNameEmailLookUp lookupUserNameAndEmail(String username, String email) {
        return userRepository.lookupUserNameAndEmail(username, email);
    }

    public String getUsernameById(Long userId) {
        return userRepository.findUsernameById(userId);
    }

    public List<DistrictUserProfilesView> getDistrictUsers() {
        return districtUserProfilesViewRepository.findAll();
    }

    public DistrictUserProfilesView getDistrictUserProfileView(Long id) {
        return districtUserProfilesViewRepository.findById(id).orElse(null);
    }

    public void deleteDistrictUserProfileById(Long id) {
        districtUserProfileRepository.deleteById(id);
    }

    public void saveDistrictUserProfile(DistrictUserProfile profile) {
        districtUserProfileRepository.save(profile);
    }

    /**
     * @return Returns users to be added to the district user profiles.
     */
    public List<DistrictUserProfileProspect> getDistrictUserProspects() {
        return districtUserProfileRepository.getDistrictUserProspects();
    }

    public void setDistrictUserProfileActive(Long profileId, boolean active) {
        districtUserProfileRepository.setActive(profileId, active);
    }

    public void setDistrictUserProfileDistrict(DistrictUserProfilesView profile, Location location) {
        districtUserProfileRepository.setDistrict(profile.getId(), location.getId());
    }
}
