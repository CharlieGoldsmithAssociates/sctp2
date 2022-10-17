<template>
  <section>

    <nav class="level">
      <!-- Left side -->
      <div class="level-left">
        <div class="level-item">
          <b-select v-model="pageSize" :loading="isLoading">
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
            <b-button v-if="canModify" @click="sendToEnrollment" icon-left="database-import" :loading="isLoading"
                      type="is-success">
              Finish And Send To Enrolment
            </b-button>
          </div>
        </div>
      </div>
    </nav>

    <b-table detailed :opened-detailed="openedDetailed" detail-key="householdId" @details-open="householdComposition"
             paginated backend-pagination :total="total" :current-page.sync="currentPage" pagination-position="both"
             :pagination-simple="false" sort-icon="menu-up" :per-page="pageSize" @page-change="onPageChange"
             backend-sorting :default-sort-direction="sortOrder" :default-sort="[sortField, sortOrder]" @sort="onSort"
             aria-next-label="Next page" aria-previous-label="Previous page" aria-page-label="Page" aria-current-label="Current page"
             :data="isEmpty ? [] : data" :striped="true" :narrowed="true" :hoverable="true" :loading="isLoading">

      <b-table-column field="mlCode" label="ML Code" sortable v-slot="props" width="8%">
        {{ props.row.mlCode }}
      </b-table-column>

      <b-table-column field="rank" label="Ranking" sortable v-slot="props" width="8%">
        {{ props.row.rank }}
      </b-table-column>

      <b-table-column field="memberCount" label="# of Members" sortable v-slot="props" width="10%">
        {{ props.row.memberCount }}
      </b-table-column>

      <b-table-column field="householdHead" label="Head of Household" sortable v-slot="props" width="10%">
        {{ props.row.householdHead }}
      </b-table-column>

      <b-table-column field="taName" label="TA" v-slot="props">
        {{ props.row.taName }}
      </b-table-column>

      <b-table-column field="villageHeadName" label="GVH Name" v-slot="props">
        {{ props.row.villageHeadName }}
      </b-table-column>

      <b-table-column field="cluserName" label="VG Cluster" v-slot="props">
        {{ props.row.clusterName }}
      </b-table-column>

      <b-table-column field="zoneName" label="Zone" v-slot="props">
        {{ props.row.zoneName }}
      </b-table-column>

      <b-table-column field="formNumber" label="Pre-Printed Num" v-slot="props">
        {{ props.row.formNumber }}
      </b-table-column>

      <b-table-column field="status" label="Status" v-slot="props">
        <b-field>
          <b-select placeholder="Select a status" size="is-small" :value="props.row.status"
                    @input="onStatusChanged(props.row, $event)" :disabled="!canModify" >
            <option
                v-for="option in statusOptions"
                :value="option"
                :key="option"
            >
              {{ option }}
            </option>
          </b-select>
        </b-field>
      </b-table-column>

      <template slot="detail" slot-scope="props">

        <section class="section">
          <h3 class="subtitle">Household Composition</h3>

          <b-table :bordered="true" :striped="true" :narrowed="true" :hoverable="true" :loading="isLoading"
                   :data="(props.row.memberList || [])">

            <b-table-column field="individualId" label="Individual Id" v-slot="props">
              {{ props.row.individualId }}
            </b-table-column>

            <b-table-column field="lastName" label="Name" v-slot="props">
              {{ `${props.row.firstName} ${props.row.lastName}` }}
            </b-table-column>

            <b-table-column field="gender" label="Gender" v-slot="props">
              {{ props.row.gender }}
            </b-table-column>

            <b-table-column field="dateOfBirth" label="D.O.B" v-slot="props">
              {{ props.row.dateOfBirth }}
            </b-table-column>

            <b-table-column field="age" label="Age" v-slot="props">
              {{ props.row.dateOfBirth }}
            </b-table-column>


            <b-table-column field="relationshipToHead" label="Relationship To Head" v-slot="props">
              {{ props.row.relationshipToHead }}
            </b-table-column>

            <template #empty>
              <div class="has-text-centered">No data to show</div>
            </template>
          </b-table>
        </section>

      </template>

      <template #empty>
        <div class="has-text-centered">No records</div>
      </template>

      <template #bottom-left>
        <div class="level-item">
          <p>
            <strong>Total</strong> {{ total }}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>PreEligible</strong> {{ statusStats.PreEligible }}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Eligible</strong> {{ statusStats.Eligible }}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Selected</strong> {{ statusStats.Selected }}
          </p>
        </div>
      </template>

      <template #top-left>
        <div class="level-item">
          <p>
            <strong>Total</strong> {{ total }}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>PreEligible</strong> {{ statusStats.PreEligible }}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Eligible</strong> {{ statusStats.Eligible }}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Selected</strong> {{ statusStats.Selected }}
          </p>
        </div>
      </template>

    </b-table>
  </section>
</template>

<script>
module.exports = {
  props: {
    sessionId: {
      type: Number,
      required: true
    },
    canModify: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      data: [],
      updatedData: new Set(),
      openedDetailed: [],
      isEmpty: false,
      isLoading: false,
      total: 0,
      sortField: 'rank',
      sortOrder: 'ASC',
      pageSize: 50,
      currentPage: 1,
      slice: false,
      statusOptions: ['PreEligible', 'Eligible', 'Selected'],
      statusStats: {
        PreEligible: 0,
        Eligible: 0,
        Selected: 0,
      }
    }
  },
  mounted() {
    this.getCbtRankings()
    this.getCbtRankingsStats()
  },
  methods: {
    getCbtRankingsStats() {
      let vm = this;
      vm.isLoading = true;

      axios.get(`/targeting/community/${vm.sessionId}/ranking-results/stats`)
          .then(function (response) {
            if (response.status === 200) {
              const hasData = response.data && response.data.length > 0;

              if (hasData) {
                if (isJsonContentType(response.headers['content-type'])) {
                  response.data.forEach(d => {
                    vm.statusStats[d.currentStatus] = d.totalCount;
                  })
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
            vm.errorDialog('There was an error loading households. Please try again');
            console.log(error);
          })
          .finally(function () {
            vm.isLoading = false;
          });
    },
    getCbtRankings() {
      let vm = this;
      vm.isLoading = true;

      const params = [
        `page=${vm.currentPage}`,
        `size=${vm.pageSize}`,
        `sort=${vm.sortField}`,
        `order=${vm.sortOrder}`,
        `slice=${vm.slice}`
      ].join('&');

      axios.get(`/targeting/community/${vm.sessionId}/ranking-results?${params}`)
          .then(function (response) {
            if (response.status === 200) {
              const hasData = response.data && response.data.length > 0;

              if (hasData) {
                if (isJsonContentType(response.headers['content-type'])) {
                  vm.data = response.data;

                  if (!vm.slice) {
                    vm.total = response.headers['x-data-total'];
                    vm.slice = true; // only get the results from DB without "size"
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
            vm.errorDialog('There was an error loading households. Please try again');
            console.log(error);
          })
          .finally(function () {
            vm.isLoading = false;
          });
    },
    async sendToEnrollment() {
      let vm = this;
      vm.isLoading = true;

      const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'], 'Content-Type': 'application/json' } };
      const errorMessage = 'Failed to send to enrollment at the moment. Please try again';

      if (vm.updatedData.size > 0) {
        const request = { cbtRankingResults: [...vm.updatedData] }

        const response = await axios.put(`/targeting/community/${vm.sessionId}/ranking-results`, JSON.stringify(request), config)
            .catch(function (error) {
              vm.isLoading = false;
              vm.errorDialog('There was an error while saving changes. Please try again');
              console.log(error);
            })

        if (response.status !== 200) {
          vm.isLoading = false;
          vm.errorDialog(errorMessage);

          return;
        }
      }

      axios.post(`/targeting/community/${vm.sessionId}/close`, null, config)
          .then(function (response) {
            console.log("response", response)
            if (response.status === 200) {
              vm.msgDialog("Targeting session closed.", "", "info");
              vm.updatedData = new Set();
              vm.canModify = false;
            } else {
              vm.errorDialog('Failed to close session. Please try again later');
              window.location.href = '/targeting/community';
            }
          })
          .catch(function (error) {
            vm.errorDialog('Error while closing session');
          })
          .finally(function () {
            vm.isLoading = false
          });
    },
    householdComposition(row) {
      let vm = this;
      vm.isLoading = true;

      axios.get(`/targeting/community/${row.cbtSessionId}/composition/${row.householdId}`)
          .then(function (response) {
            if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
              if (response.data.length > 0) {
                row.memberList = response.data;
                vm.openedDetailed[0] = row.householdId;
              } else {
                row.memberList = [];
                delete vm.openedDetailed[0]
                vm.snackbar('Household does not have any members.', 'warning')
              }
            } else {
              vm.snackbar('Server returned invalid data', 'warning');
            }
          })
          .catch(function (error) {
            vm.snackbar('Error getting household members', 'danger');
          })
          .finally(function () {
            vm.isLoading = false
          });
    },
    onStatusChanged(item, newStatus) {
      if (item.status !== newStatus) {
        this.statusStats[item.status] = this.statusStats[item.status] - 1;
        this.statusStats[newStatus] = this.statusStats[newStatus] === undefined ? 1 : this.statusStats[newStatus] + 1;

        this.updatedData.delete(item)

        item.status = newStatus;
        this.updatedData.add(item)
      }
    },
    errorDialog(msg, titleText = 'Error') {
      this.$buefy.dialog.alert({
        title: titleText,
        message: msg,
        type: 'is-danger',
        hasIcon: true,
        icon: 'times-circle',
        iconPack: 'fa',
        ariaRole: 'alertdialog',
        ariaModal: true
      })
    },
    msgDialog(msg, titleText = '', dlgType = 'info', icon = '') {
      this.$buefy.dialog.alert({
        title: titleText,
        message: msg,
        type: 'is-' + dlgType,
        hasIcon: icon !== '',
        icon: icon,
        iconPack: 'fa',
        ariaRole: 'alertdialog',
        ariaModal: true
      })
    },
    mlCode(code) {
      return code && `ML-${code}`;
    },
    onPageChange(page) {
      this.currentPage = page;
      this.getCbtRankings();
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order.toUpperCase();
      this.getCbtRankings();
    }
  }
}
</script>