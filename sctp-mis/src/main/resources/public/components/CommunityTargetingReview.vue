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
      <div v-if="updatedData.length > 0" class="level-right">
        <div class="level-item">
          <div v-if="updatedData.length > 0" class="level-right buttons">
            <b-button @click="updateCbtRankings" icon-left="database-import" :loading="isLoading"
                      type="is-primary">
              Save Changes
            </b-button>
          </div>
        </div>
      </div>
    </nav>

    <b-table paginated backend-pagination :total="total" :current-page.sync="currentPage"
             pagination-position="both" :pagination-simple="false" sort-icon="menu-up" :per-page="pageSize"
             @page-change="onPageChange" backend-sorting :default-sort-direction="sortOrder"
             :default-sort="[sortField, sortOrder]" @sort="onSort" aria-next-label="Next page"
             aria-previous-label="Previous page" aria-page-label="Page" aria-current-label="Current page"
             :data="isEmpty ? [] : data" :striped="true" :narrowed="true" :hoverable="true" :loading="isLoading">

      <b-table-column field="mlCode" label="ML Code" sortable v-slot="props" width="8%">
        {{ props.row.mlCode }}
      </b-table-column>

      <b-table-column field="rank" label="Ranking" sortable v-slot="props" width="8%">
        {{ props.row.rank }}
      </b-table-column>

      <b-table-column field="memberCount" label="# of Members" sortable v-slot="props" width="6%">
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

      <b-table-column field="prePrintedNum" label="Pre-Printed Num" v-slot="props">
        {{ props.row.prePrintedNum }}
      </b-table-column>

      <b-table-column field="status" label="Status" v-slot="props">
        <b-field>
          <b-select placeholder="Select a status" v-model="props.row.status" >
            <option
                v-for="option in props.statusOptions"
                :value="option"
                :key="option">
              {{ option }}
            </option>
          </b-select>
        </b-field>
      </b-table-column>

      <template #empty>
        <div class="has-text-centered">No records</div>
      </template>

      <template #bottom-left>
        <div class="level-item">
          <p>
            <strong>Total</strong> {{total}}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Selected</strong> {{statusStats.selected}}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Enrolled</strong> {{statusStats.enrolled}}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>PreEligible</strong> {{statusStats.preEligible}}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Eligible</strong> {{statusStats.eligible}}
          </p>
        </div>
      </template>

      <template #top-left>
        <div class="level-item">
          <p>
            <strong>Total</strong> {{total}}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Selected</strong> {{statusStats.selected}}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Enrolled</strong> {{statusStats.enrolled}}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>PreEligible</strong> {{statusStats.preEligible}}
          </p>
        </div>

        <div class="level-item">
          <p>
            <strong>Eligible</strong> {{statusStats.eligible}}
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
    statusOptions: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      data: [],
      updatedData: [],
      isEmpty: false,
      isLoading: false,
      total: 0,
      sortField: 'formNumber',
      sortOrder: 'ASC',
      pageSize: 50,
      currentPage: 1,
      slice: false,
      statusStats: {
        eligible: 0,
        selected: 0,
        preEligible: 0,
        enrolled: 0,
        nonRecertified: 0,
        ineligible: 0,
        beneficiary: 0,
      }
    }
  },
  mounted() {
    this.getCbtRankings()
  },
  methods: {
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

                  // total will never change
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
          .then(function () {
            vm.isLoading = false;
          });
    },
    updateCbtRankings() {
      let vm = this;
      vm.isLoading = true;

      const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'] } };

      axios.post(`/targeting/community/${vm.sessionId}/ranking-results/update`, vm.props.updatedData, config)
          .then(function (response) {
            const errorMessage = 'Changes cannot be saved at the moment. Please try again';

            if (response.status === 201) {
                vm.msgDialog('Changes has been queued and will be saved in the background.', '', 'success', 'check')
                window.location.href = `/targeting/community/review?session=${vm.sessionId}`;
            } else {
              vm.isLoading = false;
              vm.errorDialog(errorMessage);
            }
          })
          .catch(function (error) {
            vm.errorDialog('There was an error loading households. Please try again');
            console.log(error);
          })
          .then(function () {
            vm.isLoading = false;
          });
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

<style>

</style>