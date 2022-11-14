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

package org.cga.sctp.targeting.enrollment;

import org.cga.sctp.targeting.enrollment.validators.ValidFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Validated
public class RecipientPictureUpdateRequest {
    public static class RecipientInformation {
        @NotNull(message = "Household id is required")
        private Long householdId;

        @ValidFile(types = {"image/jpeg", "image/png"})
        private MultipartFile primaryReceiverPicture;

        @ValidFile(required = false, types = {"image/jpeg", "image/png"})
        private MultipartFile alternateReceiverPicture;

        public Long getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(Long householdId) {
            this.householdId = householdId;
        }

        public MultipartFile getPrimaryReceiverPicture() {
            return primaryReceiverPicture;
        }

        public void setPrimaryReceiverPicture(MultipartFile primaryReceiverPicture) {
            this.primaryReceiverPicture = primaryReceiverPicture;
        }

        public MultipartFile getAlternateReceiverPicture() {
            return alternateReceiverPicture;
        }

        public void setAlternateReceiverPicture(MultipartFile alternateReceiverPicture) {
            this.alternateReceiverPicture = alternateReceiverPicture;
        }
    }

    // Maximum of 500 10kb files, for each item, 20kb = 20,480bytes max per item. Total maximum bytes = 10240000 (10MB)
    @NotNull(message = "Data is required")
    @Size(min = 1, max = 50, message = "List must have between {min} and {max} items")
    private List<@NotNull RecipientInformation> data;

    public List<RecipientInformation> getData() {
        return data;
    }

    public void setData(List<RecipientInformation> data) {
        this.data = data;
    }
}
