<template>
    <section>
        <b-field grouped group-multiline>
            <b-select v-model="pageSize" :disabled="isLoading">
                <option value="5">5 per page</option>
                <option value="15">15 per page</option>
                <option value="30">30 per page</option>
                <option value="50">50 per page</option>
                <option value="100">100 per page</option>
            </b-select>
        </b-field>

        <b-table paginated backend-pagination :total="total" :current-page.sync="currentPage" pagination-position="both"
            :pagination-simple="false" sort-icon="menu-up" :per-page="pageSize" @page-change="onPageChange"
            backend-sorting :default-sort-direction="sortOrder" :default-sort="[sortField, sortOrder]" @sort="onSort"
            aria-next-label="Next page" aria-previous-label="Previous page" aria-page-label="Page"
            aria-current-label="Current page" :data="households" :striped="true" :narrowed="true" :hoverable="true"
            :loading="isLoading">


            <b-table-column field="form_number" label="Form number" sortable v-slot="props" width="8%">
                {{ props.row.form_number }}
            </b-table-column>

            <b-table-column field="ml_code" label="ML Code" sortable v-slot="props" width="8%">
                {{ `ML-${props.row.ml_code}` }}
            </b-table-column>

            <b-table-column field="member_count" label="Members" sortable v-slot="props" width="6%">
                {{ props.row.member_count }}
            </b-table-column>

            <template #empty>
                <div class="has-text-centered">No records</div>
            </template>

            <template #bottom-left>
                <div class="level-item">
                    <p>
                        <strong>Total</strong> {{ total }}
                    </p>
                </div>
            </template>

        </b-table>
    </section>
</template>

<script>
module.exports = {
    props: {
        villageCode: {
            type: Number,
            required: false,
            default: 0
        },
    },
    data() {
        return {
            isLoading: false,
            households: [],
            total: 0,
            sortField: 'form_number',
            sortOrder: 'ASC',
            pageSize: 50,
            currentPage: 1,
            slice: false,
        }
    },
    emits: ['selected'],
    computed: {

    },
    mounted() {

    },
    watch: {
        villageCode: function (newValue, oldValue) {
            if (newValue > 0 && oldValue != newValue) {
                this.slice = false;
                this.getHouseholds();
            }
        }
    },
    methods: {
        onPageChange(page) {
            this.currentPage = page;
            this.getHouseholds();
        },
        onSort(field, order) {
            this.sortField = field
            this.sortOrder = order.toUpperCase();
            this.getHouseholds();
        },
        getHouseholds() {
            let vm = this;
            vm.isLoading = true;

            const parameters = [
                `village-code=${vm.villageCode}`,
                `page=${vm.currentPage}`,
                `size=${vm.pageSize}`,
                `sort=${vm.sortField}`,
                `order=${vm.sortOrder}`,
                `slice=${vm.slice}`
            ].join('&');

            axios.get(`/targeting/households/get-by-village?${parameters}`)
                .then(function (response) {
                    if (response.status == 200) {
                        if (isJsonContentType(response.headers['content-type'])) {
                            vm.households = response.data.households;
                            if (!vm.slice) {
                                vm.total = response.data.total;
                                vm.slice = true; // only get the results from DB without "size"
                            }
                        } else {
                            throw 'invalid content type';
                        }
                    } else {
                        throw `Server returned: ${response.status}`;
                    }
                })
                .catch(function (error) {
                    vm.snackbar('Error loading households', 'danger');
                })
                .then(function () {
                    vm.isLoading = false;
                });
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
        confirm(msg, yesfn, nofn = null, title = null, type = 'primary', hasIcon = true) {
            this.$buefy.dialog.confirm({
                message: msg,
                title: title,
                hasIcon: hasIcon,
                type: 'is-' + type,
                onConfirm: () => yesfn(),
                onCancel: () => nofn && nofn()
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
        }
    }
}
</script>