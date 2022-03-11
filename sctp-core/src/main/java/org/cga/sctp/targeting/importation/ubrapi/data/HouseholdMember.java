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

package org.cga.sctp.targeting.importation.ubrapi.data;

import java.util.ArrayList;

public class HouseholdMember {
    public Long id;
    public Long household_id;
    public String first_name;
    public String last_name;
    public String date_of_birth;
    public Long gender_id;
    public Long relationship_id;
    public Long marital_status_id;
    public Long school_enrollment_id;
    public Long grade_id;
    public String school_name;
    public Long school_bursary;
    public Long education_id;
    public Long admitted_nru;
    public Long orphanage_id;
    public Long fit_for_work;
    public Long has_national_id;
    public String national_id;
    public String barcode_id;
    public Long sct_membership;
    public String sct_member_code;
    public String sct_member_number;
    public Long has_mobile;
    public String member_mobile_number;
    public Long member_inactive;
    public Object deleted_at;
    public String created_at;
    public String updated_at;
    public Gender gender;
    public Relationship relationship;
    public MaritalStatus marital_status;
    public Grade grade;
    public Orphan orphan;
    public SchoolEnrollment school_enrollment;
    public Education education;
    public ArrayList<HouseholdMemberCombinedRespons> household_member_combined_responses;
}
