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

import org.cga.sctp.utils.LocaleUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public final class UbrHouseholdImportUtil {
    private UbrHouseholdImportUtil() {}
    /**
     * Returns a predicate for matching strings within a stream
     *
     * @param keyword
     * @return
     */
    private static Predicate<String> lazyMatch(String keyword) {
        return s -> s.toLowerCase().startsWith(keyword.toLowerCase());
    }

    public static void updateAssetsLiveliHoodAndValidationErrors(UbrHouseholdImport record) {
        List<String> errors = new LinkedList<>();

        // Household Assets
        record.setHasNoAssets(record.getAssets().isEmpty());
        if (!record.getHasNoAssets()) {
            record.setHasChair(record.getAssets().stream().anyMatch(lazyMatch("Chair")));
            record.setHasRadio(record.getAssets().stream().anyMatch(lazyMatch("Radio")));
            record.setHasBicycles(record.getAssets().stream().anyMatch(lazyMatch("Bicycle")));
            record.setHasBeds(record.getAssets().stream().anyMatch(lazyMatch("Bed")));
            record.setHasMattress(record.getAssets().stream().anyMatch(lazyMatch("Mattress")));
            record.setHasSleepingMat(record.getAssets().stream().anyMatch(lazyMatch("Sleeping Mat")));
            record.setHasBlankets(record.getAssets().stream().anyMatch(lazyMatch("Blanket")));
            record.setHasWaterCan(record.getAssets().stream().anyMatch(lazyMatch("Water can")));
            record.setHasKitchenUtensils(record.getAssets().stream().anyMatch(lazyMatch("Kitchen Utensils")));
            record.setHasPoultry(record.getAssets().stream().anyMatch(lazyMatch("Poultry")));
            record.setHasLivestock(record.getAssets().stream().anyMatch(lazyMatch("Livestock")));
            record.setHasOxCart(record.getAssets().stream().anyMatch(lazyMatch("Ox Cart")));
            record.setHasHoe(record.getAssets().stream().anyMatch(lazyMatch("Hoe")));
            record.setHasMacheteKnife(record.getAssets().stream().anyMatch(lazyMatch("Panga Knife")));
            record.setHasMortar(record.getAssets().stream().anyMatch(lazyMatch("Mortar")));
            record.setHasCellphone(record.getAssets().stream().anyMatch(lazyMatch("Cellphone")));
            record.setHasLatrine(record.getAssets().stream().anyMatch(lazyMatch("Latrine")));
            record.setHasFlushToilet(record.getAssets().stream().anyMatch(lazyMatch("Flush Toilet")));
            record.setHasVipLatrine(record.getAssets().stream().anyMatch(lazyMatch("Vip Latrine")));
            record.setHasLatrineWithRoof(record.getAssets().stream().anyMatch(lazyMatch("Latrine With Roof")));
            record.setHasOtherToiletType(record.getAssets().stream().anyMatch(lazyMatch("Other Toilet Type")));
        }

        // Livelihood sources
        if (!record.getLivelihoodSources().isEmpty()) {
            record.setSurvivesOnBegging(record.getLivelihoodSources().stream().anyMatch(lazyMatch("Begging")));
            record.setSurvivesOnGanyu(record.getLivelihoodSources().stream().anyMatch(lazyMatch("Ganyu")));
            record.setSurvivesOnPettyTrading(record.getLivelihoodSources().stream().anyMatch(lazyMatch("Petty trading")));
            record.setSurvivesOnAgriculture(record.getLivelihoodSources().stream().anyMatch(lazyMatch("Agriculture")));
            record.setSurvivesOnOther(record.getLivelihoodSources().stream().anyMatch(lazyMatch("Other")));

            // Default to Other if none matched
            boolean other = record.getSurvivesOnBegging()
                    | record.getSurvivesOnGanyu()
                    | record.getSurvivesOnPettyTrading()
                    | record.getSurvivesOnAgriculture()
                    | record.getSurvivesOnOther();
            if (!other) {
                record.setSurvivesOnOther(true);
            }
        } else {
            record.setSurvivesOnOther(true);
        }

        boolean isValid = true;
        if (record.getDateOfBirth() == null) {
            isValid = false;
            errors.add("Missing date of birth");
        }

        if (LocaleUtils.isStringNullOrEmpty(record.getFirstName())
                || LocaleUtils.isStringNullOrEmpty(record.getLastName())) {
            isValid = false;
            errors.add("Missing first name and or surname.");
        }

        if (record.getFormNumber() == null) {
            isValid = false;
            errors.add("Missing form number");
        }

        if (record.getDistrictCode() == null) {
            isValid = false;
            errors.add("Missing district code");
        }

        if (record.getTaCode() == null) {
            isValid = false;
            errors.add("Missing traditional authority code");
        }

        if (record.getZoneCode() == null) {
            isValid = false;
            errors.add("Missing zone code");
        }

        if (record.getClusterCode() == null) {
            isValid = false;
            errors.add("Missing cluster code");
        }

        if (record.getVillageCode() == null) {
            isValid = false;
            errors.add("Missing village code");
        }

        List<String> recordErrors = record.getErrors();
        if (isValid) {
            if (recordErrors == null) {
                record.setErrors(Collections.emptyList());
            }
        } else {
            if (recordErrors == null) {
                record.setErrors(errors);
            } else {
                recordErrors.addAll(errors);
            }
        }

        // Set status (Only valid will be eligible for final import)
        record.setValidationStatus(isValid
                ? UbrHouseholdImport.ValidationStatus.Valid
                : UbrHouseholdImport.ValidationStatus.Error);
    }
}
