<template>
    <section>
        <b-field grouped group-multiline>
            <b-select v-model="pageSize" :loading="isLoading">
                <option value="15">15 per page</option>
                <option value="25">25 per page</option>
                <option value="50">50 per page</option>
                <option value="100">100 per page</option>
            </b-select>
        </b-field>

        <b-message type="is-info">
            Households: {{total}}<br /><br />
            <b>NOTE</b>: Archiving a household will skip importation of that household.
        </b-message>

        <b-table paginated backend-pagination :total="total" :current-page.sync="currentPage" pagination-position="both"
            :pagination-simple="false" sort-icon="menu-up" :per-page="pageSize" @page-change="onPageChange"
            backend-sorting :default-sort-direction="sortOrder" :default-sort="[sortField, sortOrder]" @sort="onSort"
            aria-next-label="Next page" aria-previous-label="Previous page" aria-page-label="Page"
            aria-current-label="Current page" :data="isEmpty ? [] : data" :striped="true" :narrowed="true"
            :hoverable="true" :loading="isLoading">

            <b-table-column field="formNumber" label="Form number" sortable v-slot="props" width="8%">
                {{ props.row.formNumber }}
            </b-table-column>

            <b-table-column field="mlCode" label="ML Code" sortable v-slot="props" width="8%">
                {{ mlCode(props.row.mlCode) }}
            </b-table-column>


            <b-table-column field="memberCount" label="Members" sortable v-slot="props" width="6%">
                {{ props.row.memberCount }}
            </b-table-column>

            <b-table-column field="errorCount" label="Member Records With Errors" sortable v-slot="props">
                <span :class="
                [
                    'tag',
                    {'is-danger': props.row.errorCount > 0},
                    {'is-success': props.row.errorCount == 0}
                ]">{{ props.row.errorCount }}</span>
            </b-table-column>

            <b-table-column field="householdHeadName" label="Household Head" v-slot="props" width="10%">
                {{ props.row.householdHeadName }}
            </b-table-column>

            <b-table-column field="districtName" label="District" v-slot="props">
                {{ props.row.districtName }}
            </b-table-column>

            <b-table-column field="traditionalAuthorityName" label="T/A" v-slot="props">
                {{ props.row.traditionalAuthorityName }}
            </b-table-column>

            <b-table-column field="cluserName" label="Cluster" v-slot="props">
                {{ props.row.cluserName }}
            </b-table-column>

            <b-table-column field="groupVillageHeadName" label="GVH" v-slot="props">
                {{ props.row.groupVillageHeadName }}
            </b-table-column>

            <b-table-column field="villageName" label="Village" v-slot="props">
                {{ props.row.villageName }}
            </b-table-column>
            <b-table-column field="archived" label="Archive" sortable v-slot="props">
                <template :v-slot="column">
                    <b-switch :ref="`switch${props.row.householdId}`" v-model="props.row.archived"
                        :value="props.row.archived"
                        @input="archiveStatusChanged(props.row.householdId, props.row.archived)" type="is-danger"
                        passive-type='passiveType'></b-switch>
                </template>
            </b-table-column>


            <template #empty>
                <div class="has-text-centered">No records</div>
            </template>

        </b-table>
    </section>
</template>

<script>

module.exports = {
    props: {
        importId: {
            type: Number,
            required: true
        },
        totalRecords: {
            type: Number,
            default: 0
        }
    },
    data() {
        return {
            data: [],
            isEmpty: false,
            isLoading: false,
            total: 0,
            sortField: 'formNumber',
            sortOrder: 'ASC',
            pageSize: 50,
            currentPage: 1,
            slice: false
        }
    },
    mounted() {
        this.getHouseholdImports();
    },
    methods: {
        archiveHousehold(household_id, archived) {
            let vm = this;
            vm.isLoading = true;

            const params = [
                `household=${household_id}`,
                `archive=${archived}`
            ].join('&');

            const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'] } };

            axios.post(`/data-import/${vm.importId}/archive-household?${params}`, null, config)
                .then(function (response) {
                    if (response.status === 200) {
                        vm.snackbar(`Successfully ${archived ? 'archived' : 'un-archived'} household`, 'success');
                    } else {
                        throw `Status: ${response.status}`;
                    }
                })
                .catch(function (error) {
                    console.log(error);
                    vm.errorDialog('Error updating data');
                })
                .then(function () {
                    vm.isLoading = false;
                });
        },
        getHouseholdImports() {
            let vm = this;
            vm.isLoading = true;
            const params = [
                `page=${vm.currentPage}`,
                `size=${vm.pageSize}`,
                `sort=${vm.sortField}`,
                `order=${vm.sortOrder}`,
                `slice=${vm.slice}`
            ].join('&');
            axios.get(`/data-import/${vm.importId}/household-imports?${params}`)
                .then(function (response) {
                    if (response.status == 200) {
                        var hasData = response.data && response.data.length > 0;

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
        mlCode(code) {
            return code && `ML-${code}`;
        },
        genderLabel(code) {
            if (typeof (code) === 'string' && code.length > 0) {
                switch (code.split(',')[0]) {
                    case '1': return 'Male';
                    case '2': return 'Female';
                }
            }
            return null;
        },
        archiveStatusChanged(hhid, status, b) {
            console.info(hhid, status, b);
            this.archiveHousehold(hhid, status)
        },
        onPageChange(page) {
            this.currentPage = page;
            this.getHouseholdImports();
        },
        onSort(field, order) {
            this.sortField = field
            this.sortOrder = order.toUpperCase();
            this.getHouseholdImports();
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
        tdAttrs(row, column) {
            console.log(row, column, this.$refs, this.$ref);
        }
    }
}
</script>
