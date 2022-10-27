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
                      type="is-danger">
              New Parameter
            </b-button>
          </div>
        </div>
      </div>
    </nav>

    <b-message type="is-info">
      <b>NOTE</b>: Archiving a household will skip importation of that household.
    </b-message>

    <b-table paginated backend-pagination :total="total" :current-page.sync="currentPage" pagination-position="both"
             :pagination-simple="false" sort-icon="menu-up" :per-page="pageSize" @page-change="onPageChange"
             backend-sorting
             :default-sort-direction="sortOrder" :default-sort="[sortField, sortOrder]" @sort="onSort"
             aria-next-label="Next page"
             aria-previous-label="Previous page" aria-page-label="Page" aria-current-label="Current page"
             :data="isEmpty ? [] : data" :striped="true" :narrowed="true" :hoverable="true" :loading="isLoading">

      <b-table-column field="id" label="ID" sortable v-slot="props" width="8%">
        {{ props.row.id }}
      </b-table-column>

      <b-table-column field="title" label="Title" sortable v-slot="props" width="8%">
        {{ props.row.title }}
      </b-table-column>

      <b-table-column field="active" label="Status" sortable v-slot="props" width="8%">
        <b-tag v-if="props.row.active" type="is-success">Active</b-tag>
        <b-tag v-if="!props.row.active" type="is-danger">Inactive</b-tag>
      </b-table-column>

      <b-table-column field="usageCount" label="Usage Count" sortable v-slot="props" width="8%">
        {{ props.row.usageCount }}
      </b-table-column>

      <b-table-column field="actions" label="Actions" v-slot="props" width="8%">
        <b-dropdown aria-role="list">
          <template #trigger="{ active }">
            <b-button label="Actions" size="is-small" type="is-primary"
                :icon-right="active ? 'menu-up' : 'menu-down'" />
          </template>


          <b-dropdown-item aria-role="list-item">View</b-dropdown-item>
          <b-dropdown-item aria-role="list-item">Add Parameters</b-dropdown-item>
          <b-dropdown-item aria-role="list-item">Delete</b-dropdown-item>
        </b-dropdown>
      </b-table-column>

    </b-table>
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
    }
  },
  methods: {
    getAllTransferParameters() {

    }
  }
}
</script>