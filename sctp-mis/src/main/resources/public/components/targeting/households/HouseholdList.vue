<template>
    <section>
        <splitpanes>
            <pane min-size="10" size="45%">
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

                    <b-table paginated backend-pagination :total="total" :current-page.sync="currentPage"
                        pagination-position="both" :pagination-simple="false" sort-icon="menu-up" :per-page="pageSize"
                        @page-change="onPageChange" backend-sorting :default-sort-direction="sortOrder"
                        :default-sort="[sortField, sortOrder]" @sort="onSort" aria-next-label="Next page"
                        aria-previous-label="Previous page" aria-page-label="Page" focusable @select="householdSelected"
                        aria-current-label="Current page" :data="households" :striped="true" :narrowed="true"
                        :hoverable="true" :loading="isLoading">


                        <b-table-column field="form_number" label="Form number" sortable v-slot="props" width="8%">
                            {{ props.row.form_number }}
                        </b-table-column>

                        <b-table-column field="ml_code" label="ML Code" sortable v-slot="props" width="8%">
                            {{ `ML-${props.row.ml_code}` }}
                        </b-table-column>

                        <b-table-column field="member_count" label="Member Count" sortable v-slot="props" width="6%">
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
            </pane>
            <pane min-size="10">
                <section>
                    <b-table sort-icon="menu-up" :data="householdMembers" :striped="true" :narrowed="true"
                        :hoverable="true" :loading="isLoading">

                        <b-table-column field="name" label="Name" sortable v-slot="props" width="8%">
                            {{ props.row.name }}
                        </b-table-column>

                        <b-table-column field="gender" label="Gender" sortable v-slot="props" width="8%">
                            {{ props.row.gender }}
                        </b-table-column>

                        <b-table-column field="dob" label="D.O.B" sortable v-slot="props">
                            {{ props.row.dob }} ({{ props.row.age }})
                        </b-table-column>

                        <b-table-column field="member_code" label="Member Code" sortable v-slot="props" >
                            ML-{{ props.row.member_code }}
                        </b-table-column>

                        <b-table-column field="national_id" label="National ID" sortable v-slot="props" >
                            {{ props.row.national_id }}
                        </b-table-column>

                        <b-table-column field="rel" label="Relationship" sortable v-slot="props">
                            {{ props.row.rel }}
                        </b-table-column>

                        <template #empty>
                            <div class="has-text-centered">This household is empty</div>
                        </template>

                        <template #bottom-left>
                            <div class="level-item">
                                <p>
                                    <strong>Total Household Members</strong> {{ selected.member_count }}
                                </p>
                            </div>
                        </template>

                    </b-table>
                </section>
            </pane>
        </splitpanes>
    </section>


</template>

<script>
const { Splitpanes, Pane } = splitpanes;

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
            selected: null
        }
    },
    emits: ['selected'],
    computed: {
        householdMembers() {
            return this.selected != null ? this.selected.members : [];
        }
    },
    components: {
        Splitpanes,
        Pane
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
        householdSelected(selectedHousehold, oldHousehold) {
            this.selected = selectedHousehold;
        },
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