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
      transfers: [ ],
    }
  },

  mounted() {
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

    initiateTransfers() {
      const vm = this;
      vm.isLoading = true;
      const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'], 'Content-Type': 'application/json' } }

      axios.post(`/transfers/periods/initiate-calculations/${this.transferPeriodId}`, {id: this.transferPeriodId}, config)
        .then(response => {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.showMessage('Initiated calculation of transfer amounts')
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
  <div class="card">
    <div class="card-header">
      <div class="card-header-title">Calculate Transfers</div>
    </div>
    <div class="card-content">
      <div class="actions">
        <button class="button is-default">Re-Run Calculations</button>&nbsp;
        <button class="button is-danger">Save Calculations</button>
      </div>
      <div v-if="transfers.length < 1" class="p">
        <div class="notification  is-link is-light m-4 is-text-3xl has-text-centered">
          <p>There are not Transfers records created for this Transfer Period.</p>
          <button @click="initiateTransfers" class="button is-warning"><i class="fa fa-add"></i> Create Transfers &amp; Run Calculations</button>
        </div>
      </div>
      <div class="table-wrapper" style="overflow-x: scroll" v-else>
        <div class="table-wrapper-inner" style="width: 200%; height: 80vh; padding-left: 50px;">
          <table class="table">
            <thead>
            <tr>
              <th>Form Number</th>
              <th>Household Head</th>
              <th>Main Receiver</th>
              <th>Alternate Receiver</th>
              <th># of Members</th>
              <th># Children</th>
              <th>Household Amount</th>
              <th>Primary Bonus</th>
              <th>Primary Incentive</th>
              <th>Seconday Bonus</th>
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
              <td>{{ t.householdHead }}</td>
              <td>{{ t.mainReceiver }}</td>
              <td>{{ t.alternateReceiver }}</td>
              <td>{{ t.numOfMembers }}</td>
              <td>{{ t.numOfChildren }}</td>
              <td>{{ t.householdAmount }}</td>
              <td>{{ t.primaryBonus }}</td>
              <td>{{ t.primaryIncentive }}</td>
              <td>{{ t.secondayBonus }}</td>
              <td v-show="enableExtraColumns.topups">{{ t.totalTopup }}</td>
              <td>{{ t.totalMonthly }}</td>
              <td>{{ t.totalAmountWithoutArrears }}</td>
              <td v-show="enableExtraColumns.arrears">{{ t.arrearsAmount }}</td>
              <td v-show="enableExtraColumns.arrears">{{ t.totalAmountWithArrears }}</td>
              <td>
                <div class="dropdown is-hoverable">
                  <div class="dropdown-trigger">
                    <button class="button button is-info is-inverted is-options" aria-haspopup="true"
                            aria-controls="dropdown-menu2">
                      <span>Options</span>
                      <span class="icon is-small">
                          <i class="fas fa-angle-down" aria-hidden="true"></i>
                        </span>
                    </button>
                  </div>
                  <div class="dropdown-menu" id="dropdown-menu2" role="menu">
                    <div class="dropdown-content">
                      <a href="#" @click="viewSingleTransfer(t.id)" class="dropdown-item">View Transfer</a>
                      <a href="#" @click="viewHouseholdDetail(t.id)" class="dropdown-item">View Household Detail</a>
                      <a href="#" @click="updateAccountNumber(t.id)" class="dropdown-item">Update Account Number</a>
                      <a href="#" @click="viewTopupsOn(t.id)" class="dropdown-item">View Topups</a>
                      <a class="divider"></a>
                      <a href="#" @click="performReconciliationOn(t.id)" class="dropdown-item">Reconcile</a>
                      <a href="#" @click="viewReconciliationStatus(t.id)" class="dropdown-item">Reconciliation Status</a>
                      <a class="divider"></a>
                      <a href="#" @click="suspendHousehold(t.id)" class="dropdown-item">Suspend Household</a>
                      <a href="#" @click="exitHousehold(t.id)" class="dropdown-item">Exit Household</a>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
