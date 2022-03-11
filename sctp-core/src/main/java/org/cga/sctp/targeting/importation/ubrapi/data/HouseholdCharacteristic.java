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

public class HouseholdCharacteristic {
    public int id;
    public int household_id;
    public int house_ownership_id;
    public int house_type_id;
    public int house_condition_id;
    public int roof_id;
    public int wall_id;
    public int floor_id;
    public Object floor_other;
    public int latrine_id;
    public Object latrine_other;
    public Object water_source_other;
    public int fuel_source_id;
    public Object fuel_source_other;
    public int land_ownership;
    public int land_ownership_type_id;
    public int irrigated_land;
    public Object irrigated_acres;
    public Object commercial_use_acres;
    public Object irrigation_system_id;
    public Object irrigation_system_other;
    public double arable_land_owned;
    public double arable_land_used;
    public int wet_land_owned;
    public int wet_land_used;
    public int characteristic_inactive;
    public Object deleted_at;
    public String created_at;
    public String updated_at;
    public Floor floor;
    public Roof roof;
    public Wall wall;
    public Latrine latrine;
    public FuelSource fuel_source;
    public HouseCondition house_condition;
}
