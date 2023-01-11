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
  props: {
    transferPeriodId: String
  },
  data() {
    return {
      applicableTopups: [],
      transferPeriod: {},
      isLoading: false,
      enableExtraColumns: {
        topups: false,
        arrears: false,
      },
      transfers: [],
    }
  },

  mounted() {
    this.loadTransfers()
  },

  methods: {

    viewSingleTransfer(transferId) {
      throw new Error('not implemented yet');
    },

    viewHouseholdDetail(transferId) {
      throw new Error('not implemented yet');
    },

    updateAccountNumber(transferId) {
      throw new Error('not implemented yet');
    },

    viewTopupsOn(transferId) {
      throw new Error('not implemented yet');
    },

    performReconciliationOn(transferId) {
      throw new Error('not implemented yet');
    },

    viewReconciliationStatus(transferId) {
      throw new Error('not implemented yet');
    },

    suspendHousehold(transferId) {
      throw new Error('not implemented yet');
    },

    exitHousehold(transferId) {
      throw new Error('not implemented yet');
    },

    loadTransfers() {
      const vm = this;
      vm.isLoading = true;
      const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'], 'Content-Type': 'application/json' } }
      axios.get(`/transfers/periods/transfer-list/${this.transferPeriodId}`, {id: this.transferPeriodId}, config)
        .then(response => {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.transfers = response.data;
          } else {
            vm.showErrorDialog('Failed to fetch transfer list', 'warning');
          }
        })
        .catch(err => {
           vm.showErrorDialog('Failed to fetch transfer list', 'warning');
        })
    },

    initiateTransfers() {
      const vm = this;
      vm.isLoading = true;
      const config = {headers: {'X-CSRF-TOKEN': csrf()['token'], 'Content-Type': 'application/json'}}

      axios.post(`/transfers/periods/initiate-calculations/${this.transferPeriodId}`, {id: this.transferPeriodId}, config)
        .then(response => {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.showMessage('Initiated calculation of transfer amounts')
            vm.loadTransfers()
          } else {
            vm.showErrorDialog('Failed to initiated calculations', 'warning');
          }
        })
        .catch(err => {
          vm.showErrorDialog('Failed to initiated calculations', 'warning');
        })
    }
  }
}
</script>
<template>
  <section>
    <div class="card">
      <div class="card-content">
        <div class="is-flex is-justify-content-space-between is-align-items-center">
          <h3 class="is-size-4">Transfer Period Information</h3>
          <div class="buttons is-flex is-flex-direction-column">
            <div class="row is-full">
              <b-button type="is-info is-light" class="is-wide">Re-Run Calculations</b-button>
              <b-button type="is-info" class="is-wide">Pay Beneficiaries</b-button>
            </div>
            <div class="row is-full">
              <b-button type="is-primary" class="is-wide">Approve Calculations</b-button>
              <b-button type="is-primary is-light" class="is-wide">Perform Reconciliation</b-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card mt-2">
      <div class="card-content">
        <b-tabs type="is-boxed">
          <b-tab-item label="Summary" class="pt-3">
            <div class="columns">
              <div class="column has-background-light p-4 mr-4 rounded">
                <h4 class="is-size-6 mb-2">Households</h4>
                <h2 class="is-size-3 has-text-weight-bold">56000</h2>
              </div>
              <div class="column has-background-light p-4 mr-4 rounded">
                <h4 class="is-size-6 mb-2">Total Transfer Amount</h4>
                <h2 class="is-size-3 has-text-weight-bold">56000</h2>
              </div>
              <div class="column has-background-light p-4 mr-4 rounded">
                <h4 class="is-size-6 mb-2">Total Topup Amount</h4>
                <h2 class="is-size-3 has-text-weight-bold">56000</h2>
              </div>
              <div class="column has-background-light p-4 rounded">
                <h4 class="is-size-6 mb-2">Total Arrears</h4>
                <h2 class="is-size-3 has-text-weight-bold">56000</h2>
              </div>
            </div>
            <div class="columns mt-4">
              <div class="column has-background-light p-4 mr-4 rounded">
                <h4 class="is-size-6 mb-2">Number of Members</h4>
                <h2 class="is-size-3 has-text-weight-bold">0</h2>
              </div>
              <div class="column has-background-light p-4 mr-4 rounded">
                <h4 class="is-size-6 mb-2">Exited Households</h4>
                <h2 class="is-size-3 has-text-weight-bold">56000</h2>
              </div>
              <div class="column has-background-light p-4 mr-4 rounded">
                <h4 class="is-size-6 mb-2">Suspended Transfers</h4>
                <h2 class="is-size-3 has-text-weight-bold">0</h2>
              </div>
              <div class="column has-background-light p-4 rounded">
                <h4 class="is-size-6 mb-2">Num Reconciled</h4>
                <h2 class="is-size-3 has-text-weight-bold">0</h2>
              </div>
            </div>
          </b-tab-item>
          <b-tab-item label="Payroll">
            <div v-if="transfers.length < 1" class="p">
              <div class="notification  is-link is-light m-4 is-text-3xl has-text-centered">
                <p>There are no Transfers records created for this Transfer Period.</p>
                <button @click="initiateTransfers" class="button is-warning"><i class="fa fa-add"></i> Create Transfers
                  &amp; Run Calculations
                </button>
              </div>
            </div>
            <div class="table-wrapper" style="overflow-x: scroll" v-else>
              <div class="table-wrapper-inner" style="width: 200%; height: 80vh; padding-left: 50px;">
                <table class="table">
                  <thead>
                  <tr>
                    <th>Form Number</th>
                    <th>ML-Code</th>
                    <th>HH Head</th>
                    <th>Main Receiver</th>
                    <th>Alt. Receiver</th>
                    <th># of Members</th>
                    <th># Children</th>
                    <th>Total Amount</th>
      <!--              <th>Primary Bonus</th>-->
      <!--              <th>Primary Incentive</th>-->
      <!--              <th>Seconday Bonus</th>-->
                    <th v-show="enableExtraColumns.topups">Total Topup</th>
                    <th>Total Monthly</th>
                    <th>Total Amount Without Arrears</th>
                    <th v-show="enableExtraColumns.arrears">Arrears Amount</th>
                    <th v-show="enableExtraColumns.arrears">Total Amount With Arrears</th>
                    <th>Options</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="t in transfers" :key="t.id">
                    <td>{{ t.formNumber }}</td>
                    <td>{{ t.householdMlCode }}</td>
                    <td>{{ t.headName }}</td>
                    <td>{{ t.main_recipient.first_name + ' ' + t.main_recipient.last_name}}</td>
                    <td>{{ t.secondary_recipient.first_name + ' ' + t.secondary_recipient.last_name}}</td>
                    <td>{{ t.householdMemberCount }}</td>
                    <td>{{ t.childrenCount }}</td>
                    <td>{{ t.totalAmountToTransfer }}</td>
      <!--              <td>{{ t.primaryBonus }}</td>-->
      <!--              <td>{{ t.primaryIncentive }}</td>-->
      <!--              <td>{{ t.secondayBonus }}</td>-->
                    <td v-show="enableExtraColumns.topups">{{ t.topupAmount }}</td>
                    <td>{{ t.monthlyAmount }}</td>
                    <td>{{ t.totalTransferAmount }}</td>
                    <td v-show="enableExtraColumns.arrears">{{ t.arrearsAmount }}</td>
                    <td v-show="enableExtraColumns.arrears">{{ t.totalAmountWithArrears }}</td>
                    <td>
                      <div class="dropdown is-hoverable">
                        <div class="dropdown-trigger">
                          <button class="button button is-info is-inverted is-options" aria-haspopup="true"
                                  aria-controls="calculate-dropdown">
                            <span>Options</span>
                            <span class="icon is-small">
                                <i class="fas fa-angle-down" aria-hidden="true"></i>
                              </span>
                          </button>
                        </div>
                        <div class="dropdown-menu" id="calculate-dropdown" role="menu">
                          <div class="dropdown-content">
                            <a :href="'/transfers/view/' + t.id" class="dropdown-item">View Transfer</a>
                            <a :href="'/households/view/' + t.id" class="dropdown-item">View Household Detail</a>
                            <a :href="'/transfers/assign-account/' + t.id" class="dropdown-item">Update Account Number</a>
                            <a :href="'/transfers/view/' + t.id" class="dropdown-item">View Topups</a>
                            <hr class="dropdown-divider">
                            <a :href="'/transfers/pay/' + t.id" class="dropdown-item">Pay Household</a>
                            <a :href="'/transfers/pay/' + t.id" class="dropdown-item">Reconcile</a>
                            <hr class="dropdown-divider">
                            <a :href="'/transfers/suspend-household/' + t.id"class="dropdown-item">Suspend Household</a>
                            <a :href="'/transfers/exit-household/' + t.id" class="dropdown-item">Exit Household</a>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </b-tab-item>
          <b-tab-item label="Topups & Parameters"></b-tab-item>
          <b-tab-item label="Transfer Agencies"></b-tab-item>
        </b-tabs>
      </div>
    </div>
  </section>
</template>
<style scoped>
.button.is-wide {
  min-width: 250px;
}

.is-flex.is-flex-gap {
  gap: 0.5rem;
}
</style>
