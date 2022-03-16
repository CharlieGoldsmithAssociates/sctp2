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

package org.cga.sctp.targeting.importation;

import org.cga.sctp.targeting.exchange.DataImport;
import org.cga.sctp.targeting.importation.parameters.*;
import org.cga.sctp.targeting.importation.ubrapi.data.Cluster;
import org.cga.sctp.targeting.importation.ubrapi.data.HouseholdMember;
import org.cga.sctp.targeting.importation.ubrapi.data.TargetingData;
import org.cga.sctp.targeting.importation.ubrapi.data.UbrApiDataResponse;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

import static java.lang.String.format;

public class UbrApiDataToHouseholdImportMapper {

    private static final int UBR_DISABILITY_PARAMETER_ID = 6;
    private static final int UBR_CHRONIC_ILLNESS_PARAMETER_ID = 7;

    public List<UbrHouseholdImport> mapFromApiData(DataImport dataImport, UbrApiDataResponse ubrApiDataResponse) {
        List<UbrHouseholdImport> result = new ArrayList<>(ubrApiDataResponse.getData().size());
        if (ubrApiDataResponse.isErrorOccurred()) {
            return Collections.emptyList();
        }

        for (TargetingData entry: ubrApiDataResponse.getData()) {
            try {
                result.addAll(mapEntry(dataImport, entry));
            } catch (Exception e) {
                LoggerFactory.getLogger(getClass()).error("Failed to map entry, got exception", e);
            }
        }

        return result;
    }

    private List<UbrHouseholdImport> mapEntry(DataImport dataImport, TargetingData targetingData) throws Exception {
        List<UbrHouseholdImport> members = new ArrayList<>(targetingData.household_members.size());
        Long batchNumber = Long.parseLong(format("%d%d%d",
                dataImport.getImporterUserId(), System.currentTimeMillis(), dataImport.getId()));

        UbrHouseholdImport common = new UbrHouseholdImport();

        common.setBatchNumber(batchNumber);
        common.setGpsLatitude(new BigDecimal(targetingData.gps_latitude));
        common.setGpsLongitude(new BigDecimal(targetingData.gps_longitude));

        common.setFloorType(FloorType.parseCode(targetingData.household_characteristic.floor.parameter_code));
        common.setRoofType(RoofType.parseCode(targetingData.household_characteristic.roof.parameter_code));
        common.setFuelSource(FuelSource.parseCode(targetingData.household_characteristic.fuel_source.parameter_code));
        // TODO:
        // common.setWaterSource(WaterSource.parseCode(targetingData.household_characteristic.water_source_other));
        common.setWallType(WallType.parseCode(targetingData.household_characteristic.wall.parameter_code));
        common.setHouseCondition(HouseCondition.parseCode(targetingData.household_characteristic.house_condition.parameter_code));
        // TODO: Review if there's a code for this?
        common.setHouseOwnership(HouseOwnership.parseIntCode(targetingData.household_characteristic.house_ownership_id));
        common.setLatrineType(LatrineType.parseCode(targetingData.household_characteristic.latrine.parameter_code));

        common.setHouseholdCode(targetingData.household_code);
        common.setPmtScore(new BigDecimal(targetingData.pmt_score));
        common.setDependencyRatio(new BigDecimal(targetingData.household_summary.dependency_ratio));
        common.setFormNumber(Long.parseLong(targetingData.form_number));
        common.setRegistrationDate(Date.valueOf(targetingData.registration_date));

        common.setVillageCode(Long.parseLong(targetingData.village.village_code));
        common.setVillageName(targetingData.village.village_name);

        common.setZoneCode(Long.parseLong(targetingData.village.zone.zone_code));
        common.setZoneName(targetingData.village.zone.zone_name);

        common.setGroupVillageHeadCode(Long.parseLong(targetingData.village.group_village_head.group_village_head_code));
        common.setGroupVillageHeadName(targetingData.village.group_village_head.group_village_head_name);

        common.setTaCode(Long.parseLong(targetingData.village.group_village_head.traditional_authority.traditional_authority_code));
        common.setTraditionalAuthorityName(targetingData.village.group_village_head.traditional_authority.traditional_authority_name);

        common.setDistrictCode(Long.parseLong(targetingData.village.group_village_head.traditional_authority.district.district_code));
        common.setDistrictName(targetingData.village.group_village_head.traditional_authority.district.district_name);

        List<Cluster> taClusters =  targetingData.village.group_village_head.traditional_authority.clusters;
        if (taClusters.size() > 1) {
            LoggerFactory.getLogger(getClass()).warn("Found more than one cluster");
        }
        Optional<Cluster> cluster = taClusters.stream().filter(c -> c.id == targetingData.village.zone.cluster_id).findFirst();
        if (cluster.isPresent()) {
            common.setClusterCode(Long.parseLong(cluster.get().cluster_code));
            common.setClusterName(cluster.get().cluster_name);
        }

        common.setAssistanceReceived(targetingData.household_food_reserve.assistance_received != 0);
        //        common.setHouseholdHeadName(/* TODO: set it! */);

        common.setCurrentHarvest(HarvestedProduceDuration.parseCode(targetingData.household_food_reserve.current_harvest.parameter_code));
        common.setLastHarvest(HarvestedProduceDuration.parseCode(targetingData.household_food_reserve.last_harvest.parameter_code));
        common.setMeals(MealsPerDay.parseCode(targetingData.household_food_reserve.meals_eaten.parameter_code));
        common.setWealthQuintile(WealthQuintile.parseString(targetingData.pmt_cut_off.wealth_quintile));

        Set<String> assets = new HashSet<>();
        targetingData.household_assets.forEach(asset -> {
            assets.add(asset.general_parameter.parameter_name);
        });

        common.setAssets(assets);

        Set<String> livelihoodSources = new HashSet<>();

        /* We don't filter the livelihood sources since we remove/filter them in
         * {@link UbrHouseholdImportUtil#updateAssetsLiveliHoodAndValidationErrors(UbrHouseholdImport)} */
        targetingData.household_combined_responses.forEach(response -> {
            livelihoodSources.add(response.general_parameter.parameter_name);
        });

        common.setLivelihoodSources(livelihoodSources);

        for (HouseholdMember householdMember: targetingData.household_members) {
            UbrHouseholdImport member = (UbrHouseholdImport) common.clone();

            member.setHouseholdId(householdMember.household_id);
            member.setHouseholdMemberId(householdMember.id);
            member.setSctMemberCode(householdMember.sct_member_code);
            member.setFirstName(householdMember.first_name);
            member.setFitForWork(householdMember.fit_for_work > 0);
            member.setLastName(householdMember.last_name);
            member.setMaritalStatus(MaritalStatus.parseCode(householdMember.marital_status.parameter_code));
            member.setGender(Gender.valueOf(householdMember.gender.parameter_name));
            member.setDateOfBirth(Date.valueOf(householdMember.date_of_birth));
            member.setMemberMobileNumber(householdMember.member_mobile_number);
            member.setNationalId(householdMember.national_id);

            member.setOrphanStatus(Orphanhood.parseCode(householdMember.orphan.parameter_code));
            member.setRelationshipToHead(RelationshipToHead.parseCode(householdMember.relationship.parameter_code));
            member.setHighestEducationLevel(EducationLevel.parseCode(householdMember.education.parameter_code));
            householdMember.household_member_combined_responses.forEach(response -> {
                if (response.general_parameter.parameter_id == UBR_DISABILITY_PARAMETER_ID) {
                    member.setDisability(Disability.parseCode(response.general_parameter.parameter_code));
                }

                if (response.general_parameter.parameter_id == UBR_CHRONIC_ILLNESS_PARAMETER_ID) {
                    member.setChronicIllness(ChronicIllness.parseCode(response.general_parameter.parameter_code));
                }
            });

            // system fields
            member.setArchived(false);
            member.setDataImportId(dataImport.getId());
            member.setBatchNumber(batchNumber);
            member.setCreatedAt(dataImport.getImportDate());
            // end system fields
            // Shared Attributes

            member.setRegistrationDate(common.getRegistrationDate());
            member.setFormNumber(common.getFormNumber());
            member.setPmtScore(common.getPmtScore());
            member.setGpsLatitude(common.getGpsLatitude());
            member.setGpsLongitude(common.getGpsLongitude());

            // This performs some rudimentary validation
            member.setAssets(common.getAssets());
            member.setLivelihoodSources(common.getLivelihoodSources());

            UbrHouseholdImportUtil.updateAssetsLiveliHoodAndValidationErrors(member);

            members.add(member);
        }

        return members;
    }
}
