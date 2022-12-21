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
  mounted() {
    this.getActiveDistricts();
    this.getOrphanhoodOptions();
    this.getDisabilityOptions();
    this.getChronicIllnessOptions();
  },
  data() {
    return {
      isLoading: false,
      funders: window.__pageData.funders || [],
      programs: window.__pageData.programs || [],
      selectedDistrict: null,
      districts: [],
      villageClusters: [],
      selectedVillageClusters: new Set(),
      traditionalAuthorities: [],
      selectedTraditionalAuthorities: new Set(),
      disabilityOptions: [],
      selectedDisabilities: new Set(),
      chronicIllnessOptions: [],
      selectedChronicIllnesses: new Set(),
      orphanhoodOptions: [],
      selectedOrphanhoodStatuses: new Set(),
      topupForm: {
        name: '',
        funderId: '',
        programId: '',
        locationType: '',
        percentage: '',
        topupType: '',
        householdStatus: '',
        active: '',
        fixedAmount: '',
        categorical: '',
        categoricalTargetingCriteriaId: '',
        discountedFromFunds: '',
        userId: '',
        categoricalTargetingLevel: '',
        ageFrom: '',
        ageTo: '',
        gender: 'both',
        taCodes: new Set(),
        clusterCodes: new Set(),
        isCategoricalTopUp: false,
        applyNextPeriod: false,
      }
    }
  },
  computed: {
    isFixedTopup() {
      return this.topupForm.topupType === 'FIXED_AMOUNT';
    },

    isPercentageTopup() {
      return this.topupForm.topupType === 'PERCENTAGE_OF_RECIPIENT_AMOUNT';
    }
  },

  methods: {
    snackbar(msg, msgType = 'info') {
      this.$buefy.toast.open({
        duration: 5000,
        message: msg,
        position: 'is-bottom',
        type: 'is-' + msgType
      })
    },
    createTopup(event) {
      event.preventDefault()
      const url = "/transfers/topups/new";
      const config = {headers: {'X-CSRF-TOKEN': csrf()['token']}};

      this.topupForm.taCodes = [...this.topupForm.taCodes];
      this.topupForm.clusterCodes = [...this.topupForm.clusterCodes];
      this.topupForm.districtCode = this.selectedDistrict.code;
      this.topupForm.chronicIllnesses = [...this.selectedChronicIllnesses].join(',');
      this.topupForm.disabilities = [...this.selectedDisabilities].join(',');
      this.topupForm.orphanhoodStatuses = [...this.selectedOrphanhoodStatuses].join(',');
      var vm = this;
      var error_message = 'Error saving topup to database'
      axios.post(url, this.topupForm, config)
        .then(response => {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            window.location = '/transfers/topups';
          } else {
            vm.snackbar(error_message, 'warning');
          }
        })
        .catch(err => {
          vm.snackbar(error_message, 'danger');
        })
      return false
    },

    getActiveDistricts() {
      const vm = this;
      vm.isLoading = true

      const error_message = 'Error getting districts'

      axios.get('/locations/districts/active')
        .then(function (response) {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.districts = response.data;
          } else {
            vm.snackbar(error_message, 'warning');
          }
        })
        .catch(function (error) {
          vm.snackbar(error_message, 'danger');
        })
        .then(function () {
          vm.isLoading = false
        });
    },

    getTraditionalAuthorities() {
      const vm = this;

      const error_message = 'Error getting child locations';

      vm.selectedTraditionalAuthorities = new Set();

      axios.get(`/locations/get-child-locations?id=${vm.selectedDistrict.code}`)
        .then(function (response) {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.traditionalAuthorities = response.data;
          } else {
            vm.snackbar(error_message, 'warning');
          }
        })
        .catch(function (error) {
          vm.snackbar(error_message, 'danger');
        })
        .then(function () {
          vm.isLoading = false
        });
    },

    getVillageClusters() {
      const vm = this;

      const error_message = 'Error getting child locations';


      vm.topupForm.clusterCodes = new Set();
      if (vm.topupForm.taCodes.size === 0) {
        vm.villageClusters = [];
        return;
      }

      axios.get(`/locations/get-child-locations/multiple?ids=${[...vm.topupForm.taCodes]}`)
        .then(function (response) {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.villageClusters = response.data;
          } else {
            vm.snackbar(error_message, 'warning');
          }
        })
        .catch(function (error) {
          vm.snackbar(error_message, 'danger');
        })
        .then(function () {
          vm.isLoading = false
        });
    },

    getDisabilityOptions() {
      const vm = this;

      axios.get('/transfers/topups/categorical/options/disabilities')
        .then(function (response) {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.disabilityOptions = response.data;
          } else {
            vm.snackbar(error_message, 'warning');
          }
        })
        .catch(function (error) {
          vm.snackbar(error_message, 'danger');
        })
        .then(function () {
          vm.isLoading = false
        });
    },

    getChronicIllnessOptions() {
      const vm = this;

      axios.get('/transfers/topups/categorical/options/chronic-illness')
        .then(function (response) {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.chronicIllnessOptions = response.data;
          } else {
            vm.snackbar(error_message, 'warning');
          }
        })
        .catch(function (error) {
          vm.snackbar(error_message, 'danger');
        })
        .then(function () {
          vm.isLoading = false
        });
    },

    getOrphanhoodOptions() {
      const vm = this;

      axios.get('/transfers/topups/categorical/options/orphanhood')
        .then(function (response) {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.orphanhoodOptions = response.data;
          } else {
            vm.snackbar(error_message, 'warning');
          }
        })
        .catch(function (error) {
          vm.snackbar(error_message, 'danger');
        })
        .then(function () {
          vm.isLoading = false
        });
    },

    range(startAt, endAt) {
      return [...Array((endAt - startAt) + 1).keys()].map(i => i + startAt);
    }
  }
}
</script>
<template>
  <div class="container">
    <!-- start content -->
    <div class="card card-default">
      <div class="card-header">
        <div class="card-header-title">New Top Up</div>
      </div>
      <div class="card-content">
        <for action="" method="post" enctype="application/x-www-form-urlencoded">
          <div class="columns">
            <div class="column is-half">
              <div class="field">
                <div class="is-normal">
                  <label class="label is-required">Top Up Name</label>
                </div>
                <div class="field-body">
                  <div class="field">
                    <div class="control">
                      <input id="name" name="name" required="required"
                             type="text"
                             v-model="topupForm.name"
                             autocomplete="off" value=""
                             minlength="1" maxlength="100" class="input">
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="column is-half">
              <div class="field">
                <div class="is-normal">
                  <label class="label is-required">Program</label>
                </div>
                <div class="field-body">
                  <div class="field">
                    <div class="select is-fullwidth">
                      <select id="programId" name="programId" v-model="topupForm.programId" class="input"
                              required="required">
                        <option disabled="disabled" selected="selected">Select Option</option>
                        <option v-for="p in programs" :value="p.id" :key="p.id">{{ p.name }}
                        </option>
                      </select>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="columns">
            <div class="column">

            </div>
            <div class="column">
              <div class="field">
                <div class="is-normal">
                  <label class="label is-required">Funder / Sponsor</label>
                </div>
                <div class="field-body">
                  <div class="field">
                    <div class="select is-fullwidth">
                      <select id="funderId" name="funderId" v-model="topupForm.funderId" class="input"
                              required="required">
                        <option disabled="disabled" selected="selected">Select Option</option>
                        <option v-for="f in funders" :value="f.id" :key="f.id">{{ f.name }}
                        </option>
                      </select>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="columns">
            <div class="column">
              <div class="field">
                <div class="is-normal">
                  <label class="label is-required">TopUp Types</label>
                </div>
                <div class="field-body">
                  <div class="field">
                    <div class="select is-fullwidth">
                      <select id="topupType" name="topupType" class="input" required="required"
                              v-model="topupForm.topupType">
                        <option disabled="disabled" selected="selected">Select Option</option>
                        <option value="FIXED_AMOUNT">Fixed Amount</option>
                        <option value="PERCENTAGE_OF_RECIPIENT_AMOUNT">% of Recipient Amount
                        </option>
                        <option value="EQUIVALENT_BENEFICIARY_AMOUNT">Current HH monthly
                          amount
                        </option>
                        <option value="EPAYMENT_ADMIN_FEE_TOPUP">E-Payment admin/cashout fee
                        </option>
                      </select>
                    </div>
                  </div>
                </div>
              </div>


            </div>

            <div class="column">
              <div class="optional-collapsible field">

                <div class="field" v-if="isFixedTopup">
                  <div class="is-normal">
                    <label class="label ">Fixed Amount Value</label>
                  </div>
                  <div class="field-body">
                    <div class="field">
                      <div class="control">
                        <input name="fixedAmount" type="number" v-model="topupForm.fixedAmount" autocomplete="off"
                               class="input">
                      </div>
                    </div>
                  </div>
                </div>

                <div class="field" v-if="isPercentageTopup">
                  <div class="is-normal">
                    <label class="label ">Percentage</label>
                  </div>
                  <div class="field-body">
                    <div class="field">
                      <div class="control">
                        <input id="percentage" name="percentage" type="text" v-model="topupForm.percentage"
                               autocomplete="off" value="" minlength="0" maxlength="3" class="input">
                      </div>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          </div>

          <div class="field">
            <div class="is-normal">
              <label class="label is-required">Household Status</label>
            </div>
            <div class="field-body">
              <div class="field">
                <div class="select is-fullwidth">
                  <select id="householdStatus" name="householdStatus" v-model="topupForm.householdStatus" class="input"
                          required="required">
                    <option disabled="disabled" selected="selected">Select Option</option>
                    <option value="BOTH">Both</option>
                    <option value="RECERTIFIED">Recertified</option>
                    <option value="NON_RECERTIFIED">Non-Recertified</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
          <b-field>
            <template slot="label">
              <span>District <span class="has-text-danger">*</span> </span>
            </template>
            <b-select placeholder="Select a district" v-model="selectedDistrict" @input="getTraditionalAuthorities"
                      expanded required>
              <option v-for="option in districts" :value="option" :key="option.id">
                {{ option.name }}
              </option>
            </b-select>
          </b-field>
          <div class="columns">
            <div class="column">
              <v-multiselect label="T/A" :options="traditionalAuthorities" option-label-field="text"
                             option-value-field="id" :selected="topupForm.taCodes"
                             @input="getVillageClusters">
              </v-multiselect>
            </div>
            <div class="column">
              <v-multiselect label="Village Clusters" :options="villageClusters" option-label-field="text"
                             option-value-field="id" :selected="topupForm.clusterCodes"></v-multiselect>
            </div>
          </div>

          <div class="categorical-topup-elements">
            <div class="option-wrapper">
              <div class="checkbox-selector">
                <label for="isCategorical">
                  <input type="checkbox" name="isCategorical" id="isCategorical" v-model="topupForm.isCategoricalTopUp">
                  &nbsp; Categorical TopUp?</label>
              </div>
              <div class="box-wrapper">
                <div class="create-categorical-topups box" v-if="topupForm.isCategoricalTopUp">
                  <div class="columns">

                    <div class="column">
                      <b-field label="Level of Calculation">
                        <b-select placeholder="Select level of calculation" v-model="topupForm.categoricalTargetingLevel"
                                  expanded required>
                          <option value="Household">By Household</option>
                          <option value="Individual">By Individual</option>
                        </b-select>
                      </b-field>
                    </div>
                  </div>
                  <div class="columns">
                    <div class="column">
                      <p class="label">Age Range</p>
                      <div class="columns">
                        <div class="column">
                          <b-field label="From" horizontal>
                            <b-select placeholder="Select a district" v-model="topupForm.ageFrom"
                                      expanded required>
                              <option value="">Age From...</option>
                              <option v-for="age in range(0, 99)" :key="age" :value="age">
                                {{ age }}
                              </option>
                            </b-select>
                          </b-field>
                        </div>
                        <div class="column">
                          <b-field label="To" horizontal>
                            <b-select placeholder="Select a district" v-model="topupForm.ageTo"
                                      expanded required>
                              <option value="">Age To...</option>
                              <option v-for="age in range(0, 99)" :key="age" :value="age">
                                {{ age }}
                              </option>
                            </b-select>
                          </b-field>
                        </div>
                      </div>
                    </div>
                    <div class="column">
                      <b-field label="Gender">
                        <b-select placeholder="Select Gender" v-model="topupForm.gender"
                                  expanded required>
                          <option value="both">Both</option>
                          <option value="male">Male</option>
                          <option value="female">Female</option>
                        </b-select>
                      </b-field>
                    </div>
                  </div>

                  <div class="columns">
                    <div class="column">
                      <v-multiselect label="Disability" :options="disabilityOptions" option-label-field="text"
                                     option-value-field="code" :selected="selectedDisabilities"></v-multiselect>
                    </div>
                    <div class="column">
                      <v-multiselect label="Chronic Illness" :options="chronicIllnessOptions"
                                     option-label-field="text"
                                     option-value-field="code" :selected="selectedChronicIllnesses"></v-multiselect>
                    </div>
                    <div class="column">
                      <v-multiselect label="Orphan Hood" :options="orphanhoodOptions" option-label-field="text"
                                     option-value-field="code" :selected="selectedOrphanhoodStatuses"></v-multiselect>
                    </div>
                  </div>
                </div>

              </div>
              <div class="column"></div>
            </div>

          </div>

          <div class="field">
            <div class="is-normal">
              <label class="label is-required">Active</label>
            </div>
            <div class="field-body">
              <div class="field">
                <div class="select">
                  <select id="active" name="active" class="input" required="required" v-model="topupForm.active">
                    <option disabled="disabled" selected="selected">Select Option</option>
                    <option value="true">Yes</option>
                    <option value="false">No</option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          <div class="apply-period block">
            <div class="field">
              <label class="checkbox-">
                <input type="checkbox" v-model="topupForm.applyNextPeriod">&nbsp; Apply to Next Transfer
                Period
              </label>
            </div>
            <p class="notification is-info" v-show="topupForm.applyNextPeriod">
              Please note that created Topups will affect subsequent transfer calculations.
            </p>
          </div>

          <div class="action-buttons">
            <!--                        <button class="button is-default" >Preview Top Up Calculation</button>&nbsp;-->
            <button class="button is-success" @click="createTopup">Create TopUp</button>
          </div>
        </for>
      </div>
    </div>
  </div>
</template>
