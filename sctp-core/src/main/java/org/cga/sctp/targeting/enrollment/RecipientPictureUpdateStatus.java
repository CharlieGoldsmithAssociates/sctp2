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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

public class RecipientPictureUpdateStatus {
    private int received;
    private int updated;
    private int failed;
    @JsonProperty("received_at")
    private ZonedDateTime receivedAt;
    @JsonProperty("completed_at")
    private ZonedDateTime completedAt;
    private final List<UpdateStatus> failedStatuses;

    public RecipientPictureUpdateStatus() {
        failedStatuses = new LinkedList<>();
    }

    public static class UpdateStatus {
        @JsonProperty("household_id")
        private long householdId;

        @JsonProperty("primary_recipient_picture_error")
        private String primaryRecipientPictureError;

        @JsonProperty("alternate_recipient_picture_error")
        private String alternateRecipientPictureError;

        public long getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(long householdId) {
            this.householdId = householdId;
        }

        public String getPrimaryRecipientPictureError() {
            return primaryRecipientPictureError;
        }

        public void setPrimaryRecipientPictureError(String primaryRecipientPictureError) {
            this.primaryRecipientPictureError = primaryRecipientPictureError;
        }

        public String getAlternateRecipientPictureError() {
            return alternateRecipientPictureError;
        }

        public void setAlternateRecipientPictureError(String alternateRecipientPictureError) {
            this.alternateRecipientPictureError = alternateRecipientPictureError;
        }
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public ZonedDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(ZonedDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public ZonedDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(ZonedDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<UpdateStatus> getFailedStatuses() {
        return failedStatuses;
    }
}
