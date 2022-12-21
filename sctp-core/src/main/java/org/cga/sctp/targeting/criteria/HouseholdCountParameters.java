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

package org.cga.sctp.targeting.criteria;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class HouseholdCountParameters {

    @NotNull(message = "Criterion is required")
    private long criterionId;

    @NotNull(message = "District code is required")
    private long districtCode;

    @NotNull(message = "Traditional authority code is required")
    private long taCode;

    @NotNull(message = "Cluster codes are required")
    @NotEmpty(message = "Cluster codes cannot be empty")
    @Size(min = 1, max = 100, message = "Must have {min} - {max} cluster codes")
    private Set<Long> clusterCodes;

    public long getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(long districtCode) {
        this.districtCode = districtCode;
    }

    public long getTaCode() {
        return taCode;
    }

    public void setTaCode(long taCode) {
        this.taCode = taCode;
    }

    public Set<Long> getClusterCodes() {
        return clusterCodes;
    }

    public void setClusterCodes(Set<Long> clusterCodes) {
        this.clusterCodes = clusterCodes;
    }

    public long getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(long criterionId) {
        this.criterionId = criterionId;
    }
}
