<!--
  - BSD 3-Clause License
  -
  - Copyright (c) 2022, CGATechnologies
  - All rights reserved.
  -
  - Redistribution and use in source and binary forms, with or without
  - modification, are permitted provided that the following conditions are met:
  -
  - 1. Redistributions of source code must retain the above copyright notice, this
  -    list of conditions and the following disclaimer.
  -
  - 2. Redistributions in binary form must reproduce the above copyright notice,
  -    this list of conditions and the following disclaimer in the documentation
  -    and/or other materials provided with the distribution.
  -
  - 3. Neither the name of the copyright holder nor the names of its
  -    contributors may be used to endorse or promote products derived from
  -    this software without specific prior written permission.
  -
  - THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  - AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  - IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  - DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  - FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  - DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  - SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  - CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  - OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  - OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  -->

<template>
  <section>
    <b-tabs expanded>
      <b-tab-item label="Household Transfer Parameters">
        <nav class="level">
          <!-- Left side -->
          <div class="level-left">
            <div class="level-item">
              <b-select v-model="householdParametersPageSize" :loading="isHouseholdParametersLoading">
                <option value="15">15 per page</option>
                <option value="25">25 per page</option>
                <option value="50">50 per page</option>
                <option value="100">100 per page</option>
              </b-select>
            </div>
          </div>

          <!-- Right side -->
          <div class="level-right">
            <div class="level-item">
              <div class="level-right buttons">
                <b-button icon-left="plus" :loading="isHouseholdParametersLoading"
                          type="is-success" @click="isAddHouseholdParametersActive = true">
                  New Household Transfer Parameter
                </b-button>
              </div>
            </div>
          </div>
        </nav>

        <b-table paginated backend-pagination :total="totalHouseholdParameters"
                 :current-page.sync="householdParametersCurrentPage"
                 pagination-position="both" :pagination-simple="false" sort-icon="menu-up"
                 :per-page="householdParametersPageSize"
                 @page-change="onHouseholdTransferPageChange" backend-sorting
                 :default-sort-direction="householdParametersSortOrder"
                 :default-sort="[householdParametersSortField, householdParametersSortOrder]"
                 @sort="onHouseholdTransferSort"
                 aria-next-label="Next page" aria-previous-label="Previous page" aria-page-label="Page"
                 aria-current-label="Current page"
                 :data="isHouseholdParametersEmpty ? [] : householdParameters" :striped="true" :narrowed="true"
                 :hoverable="true" :loading="isHouseholdParametersLoading">

          <b-table-column field="numberOfMembers" label="Number Of Members" sortable v-slot="props" width="8%">
            {{ props.row.numberOfMembers }}
          </b-table-column>

          <b-table-column field="amount" label="Amount" sortable v-slot="props" width="8%">
            {{ props.row.amount }}
          </b-table-column>

          <b-table-column field="active" label="Active" sortable v-slot="props" width="6%">
            {{ props.row.active }}
          </b-table-column>

          <b-table-column field="options" label="Options" v-slot="props" width="10%">
            Options
          </b-table-column>

          <template #empty>
            <div class="has-text-centered">No records</div>
          </template>

        </b-table>
      </b-tab-item>
      <b-tab-item label="Education Transfer Parameters">
        <div class="has-text-right">
          <b-button icon-left="plus" :loading="isEducationParametersLoading"
                    type="is-success" @click="isAddEducationParametersActive = true">
            New Education Transfer Parameter
          </b-button>
        </div>

        <b-table :data="isEducationParametersEmpty ? [] : educationParameters" :striped="true" :narrowed="true"
                 :hoverable="true" :loading="isEducationParametersLoading">

          <b-table-column field="educationLevel" label="Education Level" sortable v-slot="props" width="8%">
            {{ props.row.educationLevel }}
          </b-table-column>

          <b-table-column field="amount" label="Amount" sortable v-slot="props" width="8%">
            {{ props.row.amount }}
          </b-table-column>

          <b-table-column field="active" label="Active" sortable v-slot="props" width="6%">
            {{ props.row.active }}
          </b-table-column>

          <b-table-column field="options" label="Options" v-slot="props" width="10%">
            Options
          </b-table-column>

          <template #empty>
            <div class="has-text-centered">No records</div>
          </template>

        </b-table>
      </b-tab-item>
    </b-tabs>

    <b-modal v-model="isAddHouseholdParametersActive" trap-focus scroll="keep">
      <div class="card my-5">
        <div class="card-header">
          <p class="card-header-title">Add Household Transfer Parameters</p>
        </div>
        <div class="card-content">
          <template v-for="(child, index) in householdParametersToAdd">
            <h3 class="has-text-weight-semibold is-size-5">Parameter {{ index + 1 }}</h3>
            <hr/>
            <b-field horizontal>
              <template slot="label">
                <span>When Household Members <span class="has-text-danger">*</span> </span>
              </template>
              <b-select v-model="child.condition" placeholder="Select Option" expanded>
                <option v-for="condition in householdParametersConditions" :value="condition.name">
                  {{ `${condition.name} ( ${condition.sign} )` }}
                </option>
              </b-select>
            </b-field>

            <b-field horizontal>
              <template slot="label">
                <span>Number Of Members <span class="has-text-danger">*</span> </span>
              </template>
              <b-input v-model="child.numberOfMembers" type="number"></b-input>
            </b-field>

            <b-field horizontal>
              <template slot="label">
                <span>Amount <span class="has-text-danger">*</span> </span>
              </template>
              <b-input v-model="child.amount" type="number"></b-input>
            </b-field>

            <b-field horizontal>
              <template slot="label">
                <span>Active <span class="has-text-danger">*</span> </span>
              </template>
              <b-select v-model="child.active" placeholder="Select Option" expanded>
                <option value="Yes">Yes</option>
                <option value="No">No</option>
              </b-select>
            </b-field>
          </template>

          <div class="has-text-right mt-5">
            <b-button type="is-light" @click="isAddHouseholdParametersActive = false" :loading="isSavingHouseholdParameters">Cancel</b-button>
            <b-button type="is-info" @click="addHouseholdTransferParam"
                      :disabled="!(householdParametersToAdd[householdParametersToAdd.length - 1].active
                       && householdParametersToAdd[householdParametersToAdd.length - 1].amount
                        && householdParametersToAdd[householdParametersToAdd.length - 1].numberOfMembers
                        && householdParametersToAdd[householdParametersToAdd.length - 1].condition)"
                      :loading="isSavingHouseholdParameters">
              Add Another Parameter
            </b-button>
            <b-button type="is-success" :disabled="householdParametersToAdd.length === 1 && !(householdParametersToAdd[0].active
                       && householdParametersToAdd[0].amount && householdParametersToAdd[0].numberOfMembers  && householdParametersToAdd[0].condition)" @click="saveHouseholdParameters"
                      :loading="isSavingHouseholdParameters">
              Save Parameters
            </b-button>
          </div>
        </div>
      </div>
    </b-modal>

    <b-modal v-model="isAddEducationParametersActive" scroll="keep">
      <div class="card my-5">
        <div class="card-header">
          <p class="card-header-title">Add Education Transfer Parameters</p>
        </div>
        <div class="card-content">

          <template v-for="(child, index) in educationParametersToAdd">
            <h3 class="has-text-weight-semibold is-size-5">Parameter {{ index + 1 }}</h3>
            <hr/>
            <b-field horizontal>
              <template slot="label">
                <span>Education Level <span class="has-text-danger">*</span> </span>
              </template>
              <b-select placeholder="Select Option" v-model="child.educationLevel" expanded>
                <option v-for="level in educationParametersLevels" :value="level">
                  {{ level }}
                </option>
              </b-select>
            </b-field>

            <b-field horizontal>
              <template slot="label">
                <span>Amount <span class="has-text-danger">*</span> </span>
              </template>
              <b-input type="number" v-model="child.amount"></b-input>
            </b-field>

            <b-field horizontal>
              <template slot="label">
                <span>Active <span class="has-text-danger">*</span> </span>
              </template>
              <b-select placeholder="Select Option" v-model="child.active" expanded>
                <option value="Yes">Yes</option>
                <option value="No">No</option>
              </b-select>
            </b-field>
          </template>

          <div class="has-text-right mt-5">
            <b-button type="is-light" @click="isAddEducationParametersActive = false" :loading="isSavingEducationParameters">Cancel</b-button>
            <b-button type="is-info" @click="addEducationTransferParam"
                      :disabled="!(educationParametersToAdd[educationParametersToAdd.length - 1].active
                       && educationParametersToAdd[educationParametersToAdd.length - 1].amount
                        && educationParametersToAdd[educationParametersToAdd.length - 1].educationLevel)"
                      :loading="isSavingEducationParameters">
              Add Another Parameter
            </b-button>
            <b-button type="is-success" :disabled="educationParametersToAdd.length === 1 && !(educationParametersToAdd[0].active
                       && educationParametersToAdd[0].amount && educationParametersToAdd[0].educationLevel)" @click="saveEducationParameters"
                      :loading="isSavingEducationParameters">
              Save Parameters
            </b-button>
          </div>
        </div>
      </div>
    </b-modal>
  </section>
</template>

<script>

class EducationParameter {
  constructor(transferParameterId, educationLevel = null, amount = null, active = null) {
    this.transferParameterId = transferParameterId;
    this.educationLevel = educationLevel;
    this.amount = amount;
    this.active = active;
  }
}

class HouseholdParameter {
  constructor(transferParameterId, numberOfMembers = null, condition = null, amount = null, active = null) {
    this.transferParameterId = transferParameterId;
    this.numberOfMembers = numberOfMembers;
    this.condition = condition;
    this.amount = amount;
    this.active = active;
  }
}

module.exports = {
  props: {
    transferParameterId: {
      type: Number,
      required: true
    },
  },
  data() {
    return {
      transferParameter: null,
      educationParameters: [],
      householdParameters: [],
      educationParametersLevels: [],
      householdParametersConditions: [],
      isEducationParametersEmpty: false,
      isHouseholdParametersEmpty: false,
      isEducationParametersLoading: false,
      isHouseholdParametersLoading: false,
      totalHouseholdParameters: 0,
      householdParametersSortField: 'amount',
      householdParametersSortOrder: 'ASC',
      householdParametersPageSize: 50,
      householdParametersCurrentPage: 1,
      householdParametersSlice: false,
      isSavingEducationParameters: false,
      isSavingHouseholdParameters: false,
      isAddHouseholdParametersActive: false,
      isAddEducationParametersActive: false,
      educationParametersToAdd: [new EducationParameter(this.transferParameterId)],
      householdParametersToAdd: [new HouseholdParameter(this.transferParameterId)]
    }
  },
  mounted() {
    this.getAllEducationTransferParameters();
    this.getAllEducationTransferParametersLevels();
    this.getAllHouseholdTransferParameters();
    this.getAllHouseholdTransferParametersConditions();
  },
  methods: {
    getAllEducationTransferParameters() {
      let vm = this;
      vm.isEducationParametersLoading = true;

      axios.get(`/transfers/parameters/education/${vm.transferParameterId}/list`)
          .then(function (response) {
            if (response.status === 200) {
              let hasData = response.data && response.data.length > 0;

              if (hasData) {
                if (isJsonContentType(response.headers['content-type'])) {
                  vm.educationParameters = response.data;
                } else {
                  throw 'invalid type';
                }
              } else {
                vm.snackbar('No more data to load');
              }
            } else {
              throw `Server returned: ${response.status}`;
            }
          })
          .catch(function (error) {
            vm.errorDialog('There was an error loading transfer parameters. Please try again');
            console.log(error);
          })
          .then(function () {
            vm.isEducationParametersLoading = false;
          });
    },
    getAllHouseholdTransferParameters() {
      let vm = this;
      vm.isHouseholdParametersLoading = true;

      const params = [
        `page=${vm.householdParametersCurrentPage}`,
        `size=${vm.householdParametersPageSize}`,
        `sort=${vm.householdParametersSortField}`,
        `order=${vm.householdParametersSortOrder}`,
        `slice=${vm.householdParametersSlice}`
      ].join('&');

      axios.get(`/transfers/parameters/households/${vm.transferParameterId}/list?${params}`)
          .then(function (response) {
            if (response.status === 200) {
              let hasData = response.data && response.data.length > 0;

              if (hasData) {
                if (isJsonContentType(response.headers['content-type'])) {
                  vm.householdParameters = response.data;

                  // total will never change
                  if (!vm.slice) {
                    vm.totalHouseholdParameters = response.headers['x-data-total'];
                    vm.householdParametersSlice = true; // only get the results from DB without "size"
                  }
                } else {
                  throw 'invalid type';
                }
              } else {
                vm.snackbar('No more data to load');
              }
            } else {
              throw `Server returned: ${response.status}`;
            }
          })
          .catch(function (error) {
            vm.errorDialog('There was an error loading transfer parameters. Please try again');
            console.log(error);
          })
          .then(function () {
            vm.isHouseholdParametersLoading = false;
          });
    },
    getAllEducationTransferParametersLevels() {
      let vm = this;
      vm.isEducationParametersLoading = true;

      axios.get(`/transfers/parameters/education/levels`)
          .then(function (response) {
            if (response.status === 200) {
              let hasData = response.data && response.data.length > 0;

              if (hasData) {
                if (isJsonContentType(response.headers['content-type'])) {
                  vm.educationParametersLevels = response.data;
                } else {
                  throw 'invalid type';
                }
              } else {
                vm.snackbar('No more data to load');
              }
            } else {
              throw `Server returned: ${response.status}`;
            }
          })
          .catch(function (error) {
            vm.errorDialog('There was an error loading transfer parameters. Please try again');
            console.log(error);
          })
          .then(function () {
            vm.isEducationParametersLoading = false;
          });
    },
    getAllHouseholdTransferParametersConditions() {
      let vm = this;
      vm.isHouseholdParametersLoading = true;

      axios.get(`/transfers/parameters/households/conditions`)
          .then(function (response) {
            if (response.status === 200) {
              let hasData = response.data && response.data.length > 0;

              if (hasData) {
                if (isJsonContentType(response.headers['content-type'])) {
                  vm.householdParametersConditions = response.data;
                } else {
                  throw 'invalid type';
                }
              } else {
                vm.snackbar('No more data to load');
              }
            } else {
              throw `Server returned: ${response.status}`;
            }
          })
          .catch(function (error) {
            vm.errorDialog('There was an error loading transfer parameters. Please try again');
            console.log(error);
          })
          .then(function () {
            vm.isHouseholdParametersLoading = false;
          });
    },
    saveEducationParameters() {
      const vm = this;
      vm.isSavingEducationParameters = true


      vm.educationParametersToAdd = vm.educationParametersToAdd
          .filter(param => param.educationLevel && param.amount && param.active);

      const error_message = 'Error adding new parameter'
      const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'], 'Content-Type': 'application/json' } }

      axios.post('/transfers/parameters/education/add', JSON.stringify(this.educationParametersToAdd), config )
          .then(function (response) {
            if (response.status === 200) {

              vm.snackbar("Parameter added successfully", 'success');
              vm.isAddEducationParametersActive = false;
              vm.educationParametersToAdd = [new EducationParameter(vm.transferParameterId)];
              vm.getAllEducationTransferParameters();
            } else {
              vm.snackbar(error_message, 'warning');
            }
          })
          .catch(function (error) {
            console.log(error)
            vm.snackbar(error_message, 'danger');
          })
          .finally(function () {
            vm.isSavingEducationParameters = false
          });
    },
    saveHouseholdParameters() {
      const vm = this;
      vm.isSavingHouseholdParameters = true

      vm.householdParametersToAdd = vm.householdParametersToAdd
          .filter(param => param.numberOfMembers && param.condition && param.amount && param.active);

      const error_message = 'Error adding new parameter'
      const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'], 'Content-Type': 'application/json' } }

      axios.post('/transfers/parameters/households/add', JSON.stringify(this.householdParametersToAdd), config )
          .then(function (response) {
            if (response.status === 200) {

              vm.snackbar("Parameter added successfully", 'success');
              vm.isAddHouseholdParametersActive = false;
              vm.householdParametersToAdd = [new HouseholdParameter(vm.transferParameterId)];
              vm.getAllHouseholdTransferParameters();
            } else {
              vm.snackbar(error_message, 'warning');
            }
          })
          .catch(function (error) {
            console.log(error)
            vm.snackbar(error_message, 'danger');
          })
          .finally(function () {
            vm.isSavingHouseholdParameters = false
          });
    },
    addEducationTransferParam() {
      this.educationParametersToAdd.push(new EducationParameter(this.transferParameterId))
    },
    addHouseholdTransferParam() {
      this.householdParametersToAdd.push(new HouseholdParameter(this.transferParameterId))
    },
    onHouseholdTransferPageChange(page) {
      this.householdParametersCurrentPage = page;
      this.getAllHouseholdTransferParameters();
    },
    onHouseholdTransferSort(field, order) {
      this.householdParametersSortField = field
      this.householdParametersSortOrder = order.toUpperCase();
      this.getAllHouseholdTransferParameters();
    },
    snackbar(msg, msgType = 'info') {
      this.$buefy.toast.open({
        duration: 5000,
        message: msg,
        position: 'is-bottom',
        type: 'is-' + msgType
      })
    }
  }
}
</script>