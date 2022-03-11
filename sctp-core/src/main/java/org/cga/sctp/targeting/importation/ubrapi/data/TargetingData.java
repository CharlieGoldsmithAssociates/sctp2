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

public class TargetingData {
    public int id;
    public String registration_date;
    public String household_code;
    public String form_number;
    public Object mobile_number;
    public String gps_longitude;
    public Object gps_longitude_new;
    public String gps_latitude;
    public Object gps_latitude_new;
    public String gps_elevation;
    public Object gps_elevation_new;
    public String gps_accuracy;
    public Object gps_accuracy_new;
    public int ssp_beneficiary;
    public Object ssp_program_other;
    public int village_id;
    public int registered_via;
    public int household_status;
    public String date_approved_rejected;
    public int approved_rejected_by_id;
    public Object approved_rejected_reasons;
    public int household_inactive;
    public Object date_deactivated;
    public Object deactivated_by_id;
    public Object deleted_at;
    public String created_at;
    public String updated_at;
    public String pmt_score;
    public int pmt_cut_off_id;
    public Object export_date;
    public int export_status;
    public Object enrolment_date;
    public int enrolment_status;
    public int summaries_updated;
    public HouseholdSummary household_summary;
    public HouseholdCharacteristic household_characteristic;
    public HouseholdFoodReserve household_food_reserve;
    public ArrayList<HouseholdAsset> household_assets;
    public ArrayList<HouseholdCombinedRespons> household_combined_responses;
    public HouseholdRespondent household_respondent;
    public ArrayList<Object> household_benefits;
    public Object household_programmes;
    public ArrayList<HouseholdMember> household_members;
    public PmtCutOff pmt_cut_off;
    public Village village;
}
