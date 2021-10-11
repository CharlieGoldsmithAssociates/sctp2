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

package org.cga.sctp.targeting.importation.parameters;

public enum WaterSource implements UbrParameterValue {
    PipedIntoDwelling(1, "Piped into Dwelling"),
    PipedIntoYardOrPlot(2, "Piped into Yard/Plot"),
    CommunalStandpipe(3, "Communal Standpipe"),
    OpenWellInYardOrPlot(4, "Open Well in Yard/Plot"),
    OpenPublicWell(5, "Open Public Well"),
    ProtectedWellInYardOrPlot(6, "Protected Well in Yard/Plot"),
    ProtectedWell(7, "Protected Well"),
    Borehole(8, "Borehole"),
    Spring(9, "Spring"),
    RiverOrStream(10, "River/Stream"),
    PondOrLake(11, "Pond/Lake"),
    Dam(12, "Dam"),
    Rainwater(13, "Rainwater"),
    TankerTruckOrBowser(14, "Tanker Truck/Bowser"),
    Other(15, "Other (Specify)");

    WaterSource(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public final int code;
    public final String text;
    public static final WaterSource[] VALUES = values();

    @Override
    public String toString() {
        return text != null ? text : name();
    }

    @Override
    public int getCode() {
        return code;
    }
}
