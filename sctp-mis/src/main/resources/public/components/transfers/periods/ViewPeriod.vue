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
    <section class="my-5">
      <div class="columns">
        <div class="column">
          <h1 class="title is-6">Name:</h1>
          <h2 v-if="transferPeriod" class="subtitle has-text-weight-light is-6">{{ transferPeriod.name }}</h2>
        </div>
        <div class="column">
          <h1 class="title is-6">Description:</h1>
          <h2 v-if="transferPeriod" class="subtitle has-text-weight-light is-6">{{ transferPeriod.description }}</h2>
        </div>
      </div>

      <div class="columns">
        <div class="column">
          <h1 class="title is-6">Start Date:</h1>
          <h2 v-if="transferPeriod" class="subtitle has-text-weight-light  is-6">{{ new Date(transferPeriod.startDate).toDateString() }}</h2>
        </div>
        <div class="column">
          <h1 class="title is-6">End Date:</h1>
          <h2 v-if="transferPeriod" class="subtitle has-text-weight-light  is-6">{{ new Date(transferPeriod.endDate).toDateString() }}</h2>
        </div>
      </div>

      <div class="columns">
        <div v-if="district" class="column">
          <h1 class="title is-6">District:</h1>
          <h2 v-if="district" class="subtitle has-text-weight-light  is-6">{{ district.name }}</h2>
        </div>
      </div>

      <div class="columns">
        <div v-if="traditionalAuthorities" class="column">
          <h1 class="title is-6">Traditional Authorities:</h1>
          <b-field>
            <b-tag  v-for="ta in traditionalAuthorities" type="is-primary is-light" class="mx-1" attached>
              {{ ta.text }}
            </b-tag>
          </b-field>
        </div>
        <div v-if="villageClusters" class="column">
          <h1 class="title is-6">Village Clusters:</h1>
          <b-field>
            <b-tag v-for="vc in villageClusters"  type="is-primary is-light" class="mx-1" attached>
              {{ vc.text }}
            </b-tag>
          </b-field>
        </div>
      </div>

      <div class="columns">
        <div class="column">
          <h1 class="title is-6">State:</h1>
          <h2 v-if="transferPeriod" class="subtitle is-6">
            <b-tag v-if="!transferPeriod.closed" type="is-success is-light">Open</b-tag>
            <b-tag v-if="transferPeriod.closed" type="is-danger is-light">Closed</b-tag>
          </h2>
        </div>
      </div>
    </section>
  </section>
</template>
<script>
module.exports = {
  props: {
    transferPeriodId: {
      type: Number,
      required: true
    }
  },
  methods: {
    getTransferPeriod() {
      const vm = this;
      vm.isLoading = true

      const error_message = 'Error getting districts'

      axios.get(`/transfers/periods/${vm.transferPeriodId}`)
          .then(function (response) {
            if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
              vm.transferPeriod = response.data;
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
    getLocations(codes, isVillageCluster = false) {
      const vm = this;
      vm.isLoading = true

      const error_message = 'Error getting districts'

      axios.get(`/locations/get-locations?codes=${codes.split(',')}`)
          .then(function (response) {
            if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
              if (isVillageCluster) {
                vm.villageClusters = response.data;
              } else {
                vm.traditionalAuthorities = response.data;
              }
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
    getDistrict(code) {
      const vm = this;
      vm.isLoading = true

      const error_message = 'Error getting districts'

      axios.get(`/locations/get-location?code=${code}`)
          .then(function (response) {
            if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
              vm.district = response.data;
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
    }
  },
  watch: {
    transferPeriod(newValue) {
      if (newValue) {
        if (newValue.traditionalAuthorityCodes) {
          this.getLocations(newValue.traditionalAuthorityCodes);
        }

        if (newValue.villageClusterCodes) {
          this.getLocations(newValue.villageClusterCodes, true);
        }

        if (newValue.districtCode){
          this.getDistrict(newValue.districtCode);
        }
      }
    }
  },
  data() {
    return {
      isLoading: false,
      transferPeriod: null,
      district: null,
      traditionalAuthorities: [],
      villageClusters: [],
    }
  },
  mounted() {
    this.getTransferPeriod();
  }
}
</script>