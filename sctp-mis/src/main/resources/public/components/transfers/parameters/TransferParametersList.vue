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
            <b-button icon-left="plus" :loading="isLoading"
                      type="is-success" @click="openAddParameterModal">
              New Parameter
            </b-button>
          </div>
        </div>
      </div>
    </nav>

    <b-table paginated backend-pagination :total="total" :current-page.sync="currentPage" pagination-position="both"
             :pagination-simple="false" sort-icon="menu-up" :per-page="pageSize" @page-change="onPageChange"
             backend-sorting
             :default-sort-direction="sortOrder" :default-sort="[sortField, sortOrder]" @sort="onSort"
             aria-next-label="Next page"
             aria-previous-label="Previous page" aria-page-label="Page" aria-current-label="Current page"
             :data="isEmpty ? [] : data" :striped="true" :narrowed="true" :hoverable="true" :loading="isLoading">

      <b-table-column field="id" label="ID" v-slot="props" sortable width="8%">
        {{ props.row.id }}
      </b-table-column>

      <b-table-column field="title" label="Name" sortable v-slot="props" width="8%">
        {{ props.row.title }}
      </b-table-column>

      <b-table-column field="active" label="State" v-slot="props" width="8%">
        <b-tag v-if="props.row.active" type="is-success is-light">Active</b-tag>
        <b-tag v-if="!props.row.active" type="is-danger is-light">Inactive</b-tag>
      </b-table-column>

      <b-table-column field="usageCount" label="Usage Count" v-slot="props" width="8%">
        {{ props.row.usageCount }}
      </b-table-column>

      <b-table-column field="actions" label="Actions" v-slot="props" width="8%">
        <a :href="`/transfers/parameters/view/${props.row.id}`" class="button is-info is-small"> View </a>
        <b-button type="is-danger" size="is-small">Delete</b-button>
      </b-table-column>

    </b-table>

    <b-modal v-model="isAddParametersModalActive" trap-focus :width="640" scroll="keep">
      <div class="card">
        <div class="card-header">
          <p class="card-header-title">New Transfer Parameter</p>
        </div>
        <div class="card-content">
          <b-field horizontal>
            <template slot="label">
              <span>Program <span class="has-text-danger">*</span> </span>
            </template>
            <b-select v-model="newParameter.programId" placeholder="Select Option" expanded>
              <option v-for="program in programs" :value="program.id">
                {{ program.name }}
              </option>
            </b-select>
          </b-field>

          <b-field horizontal>
            <template slot="label">
              <span>Title <span class="has-text-danger">*</span> </span>
            </template>
            <b-input v-model="newParameter.title"></b-input>
          </b-field>

          <b-field horizontal>
            <template slot="label">
              <span>Active <span class="has-text-danger">*</span> </span>
            </template>
            <b-select v-model="newParameter.active" placeholder="Select Option" expanded>
              <option value="Yes">Yes</option>
              <option value="No">No</option>
            </b-select>
          </b-field>

          <b-field position="is-right" class="mt-5">
            <b-button @click="isAddParametersModalActive = false" type="is-light" class="mr-3">Cancel</b-button>
            <b-button :disabled="!(newParameter.title && newParameter.active && newParameter.programId)"
                      type="is-primary" @click="addNewParameter">
              Save and Define Parameters
            </b-button>
          </b-field>

        </div>
      </div>
    </b-modal>

  </section>
</template>

<script>

module.exports = {
  data() {
    return {
      data: [],
      isEmpty: false,
      isLoading: false,
      total: 0,
      sortField: 'id',
      sortOrder: 'ASC',
      pageSize: 50,
      currentPage: 1,
      slice: false,
      programs: [],
      newParameter: {programId: null, title: null, active: null},
      isAddParametersModalActive: false,
    }
  },
  mounted() {
    this.getAllTransferParameters();
  },
  methods: {
    getAllTransferParameters() {
      let vm = this;
      vm.isLoading = true;

      const params = [
        `page=${vm.currentPage}`,
        `size=${vm.pageSize}`,
        `sort=${vm.sortField}`,
        `order=${vm.sortOrder}`,
        `slice=${vm.slice}`
      ].join('&');

      axios.get(`/transfers/parameters/list?${params}`)
          .then(function (response) {
            if (response.status === 200) {
              let hasData = response.data && response.data.length > 0;

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
            vm.errorDialog('There was an error loading transfer parameters. Please try again');
            console.log(error);
          })
          .then(function () {
            vm.isLoading = false;
          });
    },
    getActivePrograms() {
      const vm = this;
      vm.isLoading = true

      const error_message = 'Error getting programs'
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
    addNewParameter() {
      const vm = this;
      vm.isLoading = true

      const error_message = 'Error adding new parameter'
      const config = {headers: {'X-CSRF-TOKEN': csrf()['token'], 'Content-Type': 'application/json'}}

      axios.post('/transfers/parameters/new', JSON.stringify(vm.newParameter), config)
          .then(function (response) {
            if (response.status === 200) {

              vm.snackbar("Parameter added successfully", 'success');
              vm.isAddParametersModalActive = false;

              window.location.href = `parameters/view/${response.data.id}`
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
    openAddParameterModal() {
      if (this.programs.length === 0) {
        this.getActivePrograms();
      }

      this.isAddParametersModalActive = true;
    },
    onPageChange(page) {
      this.currentPage = page;
      this.getAllTransferParameters();
    },
    onSort(field, order) {
      this.sortField = field
      this.sortOrder = order.toUpperCase();
      this.getAllTransferParameters();
    },
    snackbar(msg, msgType = 'info') {
      this.$buefy.toast.open({
        duration: 5000,
        message: msg,
        position: 'is-bottom',
        type: 'is-' + msgType
      })
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
  }
}
</script>