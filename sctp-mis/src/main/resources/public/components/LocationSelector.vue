<!--
  ~ BSD 3-Clause License
  ~
  ~ Copyright (c) 2022, CGATechnologies
  ~ All rights reserved.
  ~
  ~ Redistribution and use in source and binary forms, with or without
  ~ modification, are permitted provided that the following conditions are met:
  ~
  ~ 1. Redistributions of source code must retain the above copyright notice, this
  ~    list of conditions and the following disclaimer.
  ~
  ~ 2. Redistributions in binary form must reproduce the above copyright notice,
  ~    this list of conditions and the following disclaimer in the documentation
  ~    and/or other materials provided with the distribution.
  ~
  ~ 3. Neither the name of the copyright holder nor the names of its
  ~    contributors may be used to endorse or promote products derived from
  ~    this software without specific prior written permission.
  ~
  ~ THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  ~ AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  ~ IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  ~ DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  ~ FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  ~ DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  ~ SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  ~ CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  ~ OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  ~ OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  -->
<script>
module.exports = {
    data() {
        return {
            currentLevel: 'SUBNATIONAL1',
            selectedDistrictCode: '',
            selectedTraditionalAuthorities: [],
            selectedClusters: [],
            districts: [],
            traditionalAuthorities: [],
            villageClusters: [],
        }
    },

    mounted() {
        this.fetchDistricts()
    },

    methods: {
        fetchDistricts() {
            return axios.get("/locations/districts/active")
                .then(response => {
                    this.districts = response.data
                })
                .catch(err => {
                    this.showErrorMessage("Location Selector", "Failed to load Districts data...")
                })
        },

        fetchTraditionalAuthorities() {
            let locationQueryParam = this.selectedDistrictCode
            return axios.get("/locations/get-child-locations?id=" + locationQueryParam)
                .then(response => {
                    let mappedLocations = response.data.map(e => {
                        return { code: e.id, name: e.text }
                    })
                    this.traditionalAuthorities = mappedLocations
                    this.selectedTraditionalAuthorities.splice(0)
                })
                .catch(err => {
                    this.showErrorDialog("Failed to load location data...", "Failed to load location data...")
                })
        },

        fetchSublocations(codeOrArrayOfCodes, fieldName) {
            let locationQueryParam = codeOrArrayOfCodes
            if (Array.isArray(codeOrArrayOfCodes)) {
                locationQueryParam = codeOrArrayOfCodes.join(',')
            }

            return axios.get("/locations/get-child-locations?id=" + locationQueryParam)
                .then(response => {
                    let mappedLocations = response.data.map(e => {
                        return { code: e.id, name: e.text }
                    })
                    fieldName.splice(0, fieldName.length, ...mappedLocations)
                })
                .catch(err => {
                    this.showErrorDialog("Failed to load location data...", "Failed to load location data...")
                })
        },
    }
}
</script>
<template>
    <div>
        <div class="field column">
            <div class="is-normal">
                <label class="label is-required">District</label>
            </div>
            <div class="field-body">
                <div class="field">
                    <div class="select is-fullwidth">
                        <select name="districtCode"
                                class="input"
                                required="required"
                                v-model="selectedDistrictCode"
                                @change.prevent="fetchTraditionalAuthorities">
                            <option disabled="disabled" selected="selected">Select Option</option>
                            <option v-for="l in districts" :value="l.code" :key="l.code">{{ l.name }}</option>
                        </select>
                    </div>

                </div>
            </div>
        </div>
        <div class="field column">
            <div class="is-normal">
                <label class="label is-required">T/A</label>
            </div>
            <div class="field-body">
                <div class="field">
                    <div class="select is-fullwidth">
                        <select name="taCode"
                                class="input"
                                v-model="selectedTraditionalAuthorities"
                                @change.prevent="fetchSublocations(selectedTraditionalAuthorities, villageClusters)"
                        >
                            <option disabled="disabled" selected="selected">Select Option</option>
                            <option v-for="l in traditionalAuthorities" :value="l.code" :key="l.code">{{ l.name }}</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
        <div class="field column">
            <div class="is-normal">
                <label class="label is-required">Village Cluster</label>
            </div>
            <div class="field-body">
                <div class="field">
                    <div class="select is-fullwidth">
                        <select name="clusterCode" class="input" v-model="selectedClusters" multiple>
                            <option disabled="disabled" selected="selected">Select Option</option>
                            <option v-for="l in villageClusters" :value="l.code" :key="l.code">{{ l.name }}</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>