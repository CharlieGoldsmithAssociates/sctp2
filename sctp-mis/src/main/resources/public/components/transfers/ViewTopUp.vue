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
  <div class="card no-overlap">
    <header class="card-header">
      <p class="card-header-title">View Top Up</p>
    </header>
    <div v-if="topup" class="card-content">
      <div class="columns">
        <div class="column">
          <h1 class="title is-6">Name:</h1>
          <h2 v-if="topup" class="subtitle is-6">{{ topup.name }}</h2>
        </div>
        <div class="column">
          <h1 class="title is-6">State:</h1>
          <h2 v-if="topup" class="subtitle is-6">
            <b-tag v-if="topup.active" type="is-success is-light">Active</b-tag>
            <b-tag v-if="!topup.active" type="is-danger is-light">Inactive</b-tag>
          </h2>
        </div>
      </div>

      <div class="columns">
        <div class="column">
          <h1 class="title is-6">Program Name:</h1>
          <h2 v-if="topup" class="subtitle is-6">{{ topup.programName }}</h2>
        </div>
        <div class="column">
          <h1 class="title is-6">Funder:</h1>
          <h2 v-if="topup" class="subtitle is-6">{{ topup.funderName }}</h2>
        </div>
      </div>

      <div class="columns">
        <div class="column">
          <h1 class="title is-6">District:</h1>
          <h2 v-if="topup" class="subtitle is-6">{{ topup.districtName }}</h2>
        </div>
      </div>

<!--      <div class="field"><label>T / A</label>-->
<!--        <div>{{ topup.taCodes }}</div>-->
<!--      </div>-->
<!--      <div class="field"><label>Village Cluster</label>-->
<!--        <div>{{ topup.clusterCodes }}</div>-->
<!--      </div>-->

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
          <h1 class="title is-6">Topup Type:</h1>
          <h2 v-if="topup" class="subtitle is-6">{{ topup.topupType.replaceAll("_", " ") }}</h2>
        </div>

        <div class="column">
          <h1 class="title is-6">Amount:</h1>
          <h2 v-if="topup && topup.topupType === 'FIXED_AMOUNT'" class="subtitle is-6">{{ topup.fixedAmount }}</h2>
          <h2 v-if="topup && topup.topupType === 'PERCENTAGE_OF_RECIPIENT_AMOUNT'" class="subtitle is-6">{{ topup.percentage }} %</h2>
        </div>
      </div>

      <div class="columns">
        <div class="column">
          <h1 class="title is-6">Is Categorical:</h1>
          <h2 v-if="topup" class="subtitle is-6">{{ topup.categorical }}</h2>
        </div>
      </div>


      <section>
        <h1 v-if="topup.categorical" class="title is-6">Categorical TopUp Parameters:</h1>

        <!--        {# TODO: <div>{{ topup.chronicIllnesses}}</div>#}-->
        <!--        {# TODO: <div>{{ topup.chronicIllnesses}}</div>#}-->
        <!--        {# TODO: <div>{{ topup.chronicIllnesses}}</div>#}-->
        <!--        {# TODO: <div>{{ topup.chronicIllnesses}}</div>#}-->

      </section>

      <div class="columns">
        <div class="column">
          <h1 class="title is-6">Is Executed?:</h1>
          <h2 v-if="topup" class="subtitle is-6">{{ topup.executed }}</h2>
        </div>

        <div class="column">
          <h1 class="title is-6">Amount Executed:</h1>
          <h2 class="subtitle is-6">{{ topup.amountExecuted }}</h2>
        </div>
      </div>
    </div>
  </div>
  <br/>
  <div class="card no-overlap">
    <div class="card-header">
      <div class="card-header-title">Topup Applies to following Transfer Periods </div>
    </div>
    <div class="card-content">
      <table class="table is-striped is-fullwidth">
        <thead>
        <tr>
          <th>Program</th>
          <th>Location (TA > Village Cluster)</th>
          <th>Amount Projected</th>
          <th>Executed On</th>
        </tr>
        </thead>
        <tbody>
        {# TODO: load and display estimated amounts for the periods/locations this topup applies to  #}
        </tbody>
      </table>

    </div>
  </div>
</template>
<script>
module.exports = {
  props: {
    topupId: {
      type: Number,
      required: true
    }
  },
  methods: {
    getTopUp() {
      const vm = this;

      const error_message = 'Error getting child locations';

      vm.selectedTraditionalAuthorities = new Set();

      axios.get(`/transfers/topups/${vm.topupId}`)
        .then(function (response) {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            console.log(response.data)
            vm.topup = response.data;
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
  },
  watch: {
    topup(newValue) {
      if (newValue) {
        if (newValue.taCodes) {
          this.getLocations(newValue.taCodes);
        }

        if (newValue.clusterCodes) {
          this.getLocations(newValue.clusterCodes, true);
        }
      }
    }
  },
  data() {
    return {
      topup: null,
      villageClusters: [],
      traditionalAuthorities: []
    }
  },
  mounted() {
    this.getTopUp();
  },
}
</script>