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

package org.cga.sctp.mis.transfers;

import org.cga.sctp.program.Program;
import org.cga.sctp.targeting.enrollment.EnrollmentSessionView;
import org.cga.sctp.transfers.TransferEventHouseholdView;
import org.cga.sctp.transfers.TransferSession;
import org.cga.sctp.transfers.parameters.EducationTransferParameter;
import org.cga.sctp.transfers.parameters.HouseholdTransferParameter;

import java.util.List;
import java.util.Map;

public class TransferCalculationPageData {
    private TransferSession transferSession;
    private EnrollmentSessionView enrollmentSession;
    private Program programInfo;
    private List<TransferEventHouseholdView> householdRows;
    private List<Object> transferPeriods; // TODO
    private List<HouseholdTransferParameter> householdParams;
    private Map<String, EducationTransferParameter> educationParams;

    public TransferSession getTransferSession() {
        return transferSession;
    }

    public void setTransferSession(TransferSession transferSession) {
        this.transferSession = transferSession;
    }

    public EnrollmentSessionView getEnrollmentSession() {
        return enrollmentSession;
    }

    public void setEnrollmentSession(EnrollmentSessionView enrollmentSession) {
        this.enrollmentSession = enrollmentSession;
    }

    public Program getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(Program programInfo) {
        this.programInfo = programInfo;
    }

    public List<TransferEventHouseholdView> getHouseholdRows() {
        return householdRows;
    }

    public void setHouseholdRows(List<TransferEventHouseholdView> householdRows) {
        this.householdRows = householdRows;
    }

    public List<Object> getTransferPeriods() {
        return transferPeriods;
    }

    public void setTransferPeriods(List<Object> transferPeriods) {
        this.transferPeriods = transferPeriods;
    }

    public List<HouseholdTransferParameter> getHouseholdParams() {
        return householdParams;
    }

    public void setHouseholdParams(List<HouseholdTransferParameter> householdParams) {
        this.householdParams = householdParams;
    }

    public Map<String, EducationTransferParameter> getEducationParams() {
        return educationParams;
    }

    public void setEducationParams(Map<String, EducationTransferParameter> educationParams) {
        this.educationParams = educationParams;
    }
}
