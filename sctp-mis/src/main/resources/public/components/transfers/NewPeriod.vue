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
      isLoading: false,
      newPeriodForm: {},
      endDate: new Date(),
      startDate: new Date(),
      periodMonthlyView: {},
      selectedDistrict: null,
      districts: [{id: 1, name: 'Dedza'}],
      selectedProgram: null,
      programs: [{id: 1, name: 'SCTP'}]
    }
  },
  mounted() {
    this.getActivePrograms();
    this.getActiveDistricts();
  },
  watch: {
    startDate: function (newDate) {
      this.getPeriodInMonths(newDate, this.endDate);
    },
    endDate: function (newDate) {
      this.getPeriodInMonths(this.startDate, newDate);
    }
  },
  methods: {
    clearDate(isStartDate) {
      if (isStartDate) {
        this.startDate = null;
      } else {
        this.endDate = null;
      }
    },
    getPeriodInMonths(startDate, endDate) {
      const period = {}
      if (startDate && endDate) {
        let currentDate = new Date(startDate);
        while (currentDate < endDate) {
          const year = currentDate.getFullYear();

          if (!period[year]) {
            period[year] = new Array(12);
          }

          period[year][currentDate.getMonth()] = 1;
          currentDate.setMonth(currentDate.getMonth() + 1);
        }
      }

      this.periodMonthlyView = period
    },
    getActivePrograms() {
      const vm = this;
      vm.isLoading = true

      const error_message = 'Error getting districts'

      axios.get('/programs/active')
          .then(function (response) {
            if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
              vm.programs = response.data;
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
    openTransferPeriod() {
      const vm = this;
      vm.isLoading = true;

      const error_message = 'Failed to open new period'
      const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'], 'Content-Type': 'application/json' } }

      const requestBody = {
        programId: vm.selectedProgram,
        districtId: vm.selectedDistrict,
        startDate: this.formatDate(vm.startDate),
        endDate: this.formatDate(vm.endDate)
      }
      axios.post('/transfers/periods/open-new', JSON.stringify(requestBody), config)
          .then(function (response) {
            if (response.status === 200) {
              vm.districts = response.data;
              window.location.href = '/transfers/periods'
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
    formatDate(date) {
      let month = '' + (date.getMonth() + 1),
          day = '' + date.getDate(),
          year = date.getFullYear();

      if (month.length < 2)
        month = '0' + month;

      if (day.length < 2)
        day = '0' + day;

      return [year, month, day].join('-');
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
<template>
  <div class="card-content">
    <div class="columns-stuff">
      <div class="columns">
        <div class="column is-half">
          <b-field>
            <template slot="label">
              <span>Start Date <span class="has-text-danger">*</span> </span>
            </template>
            <b-datepicker
                class="is-small"
                v-model="startDate"
                placeholder="Click to select..."
                icon="calendar-today"
                :icon-right="startDate ? 'close-circle' : ''"
                icon-right-clickable
                @icon-right-click="clearDate(true)"
                trap-focus>
            </b-datepicker>
          </b-field>
        </div>
        <div class="column is-half">
          <b-field>
            <template slot="label">
              <span>End Date <span class="has-text-danger">*</span> </span>
            </template>
            <b-datepicker
                class="is-small"
                v-model="endDate"
                placeholder="Click to select..."
                icon="calendar-today"
                :icon-right="endDate ? 'close-circle' : ''"
                icon-right-clickable
                @icon-right-click="clearDate(false)"
                trap-focus>
            </b-datepicker>
          </b-field>
        </div>
      </div>
      <b-field>
        <template slot="label">
          <span>Program <span class="has-text-danger">*</span> </span>
        </template>
        <b-select placeholder="Select a programs" v-model="selectedProgram" expanded required>
          <option
              v-for="option in programs"
              :value="option.id"
              :key="option.id">
            {{ option.name }}
          </option>
        </b-select>
      </b-field>
      <b-field>
        <template slot="label">
          <span>District <span class="has-text-danger">*</span> </span>
        </template>
        <b-select placeholder="Select a district" v-model="selectedDistrict" expanded required>
          <option
              v-for="option in districts"
              :value="option.id"
              :key="option.id">
            {{ option.name }}
          </option>
        </b-select>
      </b-field>
      <div class="buttons is-right mt-5">
        <a class="button" href="/transfers/periods">Cancel</a>
        <button @click="openTransferPeriod" class="button is-success" :disabled="!(selectedDistrict && selectedProgram)" >Open Transfer Period &gt;&gt;</button>
      </div>
      <div class="period-parameters" v-if="Object.keys(periodMonthlyView).length > 0">
        <div class="new-transfer-period-params has-text-centered">
          <div class="month-preview">
            <table class="table is-full-width is-striped">
              <thead>
              <tr>
                <th></th>
                <th>Jan</th>
                <th>Feb</th>
                <th>Mar</th>
                <th>Apr</th>
                <th>May</th>
                <th>Jun</th>
                <th>Jul</th>
                <th>Aug</th>
                <th>Sep</th>
                <th>Oct</th>
                <th>Nov</th>
                <th>Dec</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="period in Object.keys(periodMonthlyView)" class="is-full-width ">
                <td>{{ period }}</td>
                <td v-for="month in periodMonthlyView[period]">
                  {{ month && '&times;' }}
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>