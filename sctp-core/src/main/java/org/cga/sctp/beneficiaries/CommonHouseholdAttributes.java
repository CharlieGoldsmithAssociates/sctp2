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

package org.cga.sctp.beneficiaries;


import org.cga.sctp.targeting.importation.parameters.WaterSource;
import org.cga.sctp.targeting.importation.converters.WaterSourceParameterValueConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class CommonHouseholdAttributes {

    // Assets
    private Boolean hasChair;
    private Boolean hasRadio;
    private Boolean hasBicycles;
    private Boolean hasBeds;
    private Boolean hasMattress;
    private Boolean hasSleepingMat;
    private Boolean hasBlankets;
    private Boolean hasWaterCan;

    @Convert(converter = WaterSourceParameterValueConverter.class)
    private WaterSource waterSource;

    @Column(name = "has_kitchen_utencils")
    private Boolean hasKitchenUtensils;
    private Boolean hasPoultry;
    private Boolean hasLivestock;
    private Boolean hasOxCart;
    private Boolean hasHoe;
    private Boolean hasMacheteKnife;
    private Boolean hasMortar;
    private Boolean hasCellphone;
    private Boolean hasNoAssets;
    private Boolean hasLatrine;
    private Boolean hasFlushToilet;
    private Boolean hasVipLatrine;
    private Boolean hasLatrineWithRoof;
    private Boolean hasOtherToiletType;

    private Boolean survivesOnBegging;
    private Boolean survivesOnGanyu;
    private Boolean survivesOnPettyTrading;
    private Boolean survivesOnAgriculture;
    private Boolean survivesOnOther;

    public Boolean getHasChair() {
        return hasChair;
    }

    public void setHasChair(Boolean hasChair) {
        this.hasChair = hasChair;
    }

    public Boolean getHasRadio() {
        return hasRadio;
    }

    public void setHasRadio(Boolean hasRadio) {
        this.hasRadio = hasRadio;
    }

    public Boolean getHasBicycles() {
        return hasBicycles;
    }

    public void setHasBicycles(Boolean hasBicycles) {
        this.hasBicycles = hasBicycles;
    }

    public Boolean getHasBeds() {
        return hasBeds;
    }

    public void setHasBeds(Boolean hasBeds) {
        this.hasBeds = hasBeds;
    }

    public Boolean getHasMattress() {
        return hasMattress;
    }

    public void setHasMattress(Boolean hasMattress) {
        this.hasMattress = hasMattress;
    }

    public Boolean getHasSleepingMat() {
        return hasSleepingMat;
    }

    public void setHasSleepingMat(Boolean hasSleepingMat) {
        this.hasSleepingMat = hasSleepingMat;
    }

    public Boolean getHasBlankets() {
        return hasBlankets;
    }

    public void setHasBlankets(Boolean hasBlankets) {
        this.hasBlankets = hasBlankets;
    }

    public Boolean getHasWaterCan() {
        return hasWaterCan;
    }

    public void setHasWaterCan(Boolean hasWaterCan) {
        this.hasWaterCan = hasWaterCan;
    }

    public Boolean getHasKitchenUtensils() {
        return hasKitchenUtensils;
    }

    public void setHasKitchenUtensils(Boolean hasKitchenUtensils) {
        this.hasKitchenUtensils = hasKitchenUtensils;
    }

    public Boolean getHasPoultry() {
        return hasPoultry;
    }

    public void setHasPoultry(Boolean hasPoultry) {
        this.hasPoultry = hasPoultry;
    }

    public Boolean getHasLivestock() {
        return hasLivestock;
    }

    public void setHasLivestock(Boolean hasLivestock) {
        this.hasLivestock = hasLivestock;
    }

    public Boolean getHasOxCart() {
        return hasOxCart;
    }

    public void setHasOxCart(Boolean hasOxCart) {
        this.hasOxCart = hasOxCart;
    }

    public Boolean getHasHoe() {
        return hasHoe;
    }

    public void setHasHoe(Boolean hasHoe) {
        this.hasHoe = hasHoe;
    }

    public Boolean getHasMacheteKnife() {
        return hasMacheteKnife;
    }

    public void setHasMacheteKnife(Boolean hasMacheteKnife) {
        this.hasMacheteKnife = hasMacheteKnife;
    }

    public Boolean getHasMortar() {
        return hasMortar;
    }

    public void setHasMortar(Boolean hasMortar) {
        this.hasMortar = hasMortar;
    }

    public Boolean getHasCellphone() {
        return hasCellphone;
    }

    public void setHasCellphone(Boolean hasCellphone) {
        this.hasCellphone = hasCellphone;
    }

    public Boolean getHasNoAssets() {
        return hasNoAssets;
    }

    public void setHasNoAssets(Boolean hasNoAssets) {
        this.hasNoAssets = hasNoAssets;
    }

    public Boolean getHasLatrine() {
        return hasLatrine;
    }

    public void setHasLatrine(Boolean hasLatrine) {
        this.hasLatrine = hasLatrine;
    }

    public Boolean getHasFlushToilet() {
        return hasFlushToilet;
    }

    public void setHasFlushToilet(Boolean hasFlushToilet) {
        this.hasFlushToilet = hasFlushToilet;
    }

    public Boolean getHasVipLatrine() {
        return hasVipLatrine;
    }

    public void setHasVipLatrine(Boolean hasVipLatrine) {
        this.hasVipLatrine = hasVipLatrine;
    }

    public Boolean getHasLatrineWithRoof() {
        return hasLatrineWithRoof;
    }

    public void setHasLatrineWithRoof(Boolean hasLatrineWithRoof) {
        this.hasLatrineWithRoof = hasLatrineWithRoof;
    }

    public Boolean getHasOtherToiletType() {
        return hasOtherToiletType;
    }

    public void setHasOtherToiletType(Boolean hasOtherToiletType) {
        this.hasOtherToiletType = hasOtherToiletType;
    }

    public Boolean getSurvivesOnBegging() {
        return survivesOnBegging;
    }

    public void setSurvivesOnBegging(Boolean survivesOnBegging) {
        this.survivesOnBegging = survivesOnBegging;
    }

    public Boolean getSurvivesOnGanyu() {
        return survivesOnGanyu;
    }

    public void setSurvivesOnGanyu(Boolean survivesOnGanyu) {
        this.survivesOnGanyu = survivesOnGanyu;
    }

    public Boolean getSurvivesOnPettyTrading() {
        return survivesOnPettyTrading;
    }

    public void setSurvivesOnPettyTrading(Boolean survivesOnPettyTrading) {
        this.survivesOnPettyTrading = survivesOnPettyTrading;
    }

    public Boolean getSurvivesOnAgriculture() {
        return survivesOnAgriculture;
    }

    public void setSurvivesOnAgriculture(Boolean survivesOnAgriculture) {
        this.survivesOnAgriculture = survivesOnAgriculture;
    }

    public Boolean getSurvivesOnOther() {
        return survivesOnOther;
    }

    public void setSurvivesOnOther(Boolean survivesOnOther) {
        this.survivesOnOther = survivesOnOther;
    }

    public WaterSource getWaterSource() {
        return waterSource;
    }

    public void setWaterSource(WaterSource waterSource) {
        this.waterSource = waterSource;
    }
}
