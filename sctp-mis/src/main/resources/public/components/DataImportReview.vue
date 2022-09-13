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
            <div v-if="canModify" class="level-right">
                <div class="level-item">
                    <div v-if="canModify" class="level-right buttons">
                        <b-button :loading="isLoading" tag="a" icon-left="microsoft-excel"
                            :href="`/data-import/export-errors/${importId}`" target="_blank" type="is-primary">Export
                            Errors To Excel</b-button>
                        <b-button @click="queueImportMerge" icon-left="database-import" :loading="isLoading"
                            type="is-danger">
                            Finish & Merge
                        </b-button>
                    </div>
                </div>
            </div>
        </nav>

        <b-message type="is-info">
            <b>NOTE</b>: Archiving a household will skip importation of that household.
        </b-message>

        <b-table :has-detailed-visible="(row) => !row.archiveHousehold" detailed :opened-detailed="openedDetailed"
            detail-key="householdId" @details-open="memberDetails" paginated backend-pagination :total="total"
            :current-page.sync="currentPage" pagination-position="both" :pagination-simple="false" sort-icon="menu-up"
            :per-page="pageSize" @page-change="onPageChange" backend-sorting :default-sort-direction="sortOrder"
            :default-sort="[sortField, sortOrder]" @sort="onSort" aria-next-label="Next page"
            aria-previous-label="Previous page" aria-page-label="Page" aria-current-label="Current page"
            :data="isEmpty ? [] : data" :striped="true" :narrowed="true" :hoverable="true" :loading="isLoading">

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

            <b-table-column field="hasHouseholdHead" label="Household Head" sortable v-slot="props" width="10%">
                {{ props.row.householdHeadName }}
            </b-table-column>

            <b-table-column field="districtName" label="District" v-slot="props">
                {{ props.row.districtName }}
            </b-table-column>

            <b-table-column field="traditionalAuthorityName" label="T/A" v-slot="props">
                {{ props.row.traditionalAuthorityName }}
            </b-table-column>

            <b-table-column field="cluserName" label="Cluster" v-slot="props">
                {{ props.row.clusterName }}
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
                        :value="props.row.archived" :disabled="!canModify"
                        @input="archiveStatusChanged(props.row.householdId, props.row.archived)" type="is-danger"
                        passive-type='passiveType'></b-switch>
                </template>
            </b-table-column>


            <template slot="detail" slot-scope="props">

                <section class="section">
                    <h3 class="subtitle">Select a member to make the head of the household</h3>

                    <b-table :bordered="true" :striped="true" :narrowed="true" :hoverable="true" :loading="isLoading"
                        :data="(props.row.memberList || [])">
                        <b-table-column field="name" label="Name" v-slot="p">
                            <b-radio :expanded="true" v-model="props.row.householdHeadMemberId" name="member"
                                @input="setHouseholdHead(p.row, props.row)"
                                :disabled="props.row.hasHouseholdHead"
                                :native-value="p.row.householdMemberId">
                                {{ p.row.name }}
                            </b-radio>
                        </b-table-column>

                        <b-table-column field="gender" label="Gender" v-slot="props">
                            {{ props.row.gender }}
                        </b-table-column>

                        <b-table-column field="dateOfBirth" label="D.O.B" v-slot="props">
                            {{ props.row.dateOfBirth }}
                        </b-table-column>

                        <b-table-column field="nationalId" label="National ID" v-slot="props">
                            {{ props.row.nationaId }}
                        </b-table-column>

                        <b-table-column field="relationship" label="Relationship" v-slot="props">
                            {{ props.row.relationship }}
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
                        <strong>Total</strong> {{total}}
                    </p>
                </div>

                <div class="level-item">
                    <p>
                        <strong>Without a head</strong> {{householdStats.noHead}}
                    </p>
                </div>

                <div class="level-item">
                    <p>
                        <strong>Archived</strong> {{householdStats.archived}}
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
                        <strong>Without a head</strong> {{householdStats.noHead}}
                    </p>
                </div>

                <div class="level-item">
                    <p>
                        <strong>Archived</strong> {{householdStats.archived}}
                    </p>
                </div>
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
        importSource: {
            type: String,
            required: true
        },
        canModify: {
            type: Boolean,
            required: true,
            default: false
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
            slice: false,
            openedDetailed: [],
            householdStats: { noHead: 0, archived: 0 }
        }
    },
    mounted() {
        this.getHouseholdImports();
        this.getHouseholdCountWithoutHead();
    },
    methods: {
        archiveHousehold(household_id, archived) {
            let vm = this;
            vm.isLoading = true;

            const params = [
                `household-id=${household_id}`,
                `archive=${archived}`
            ].join('&');

            const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'] } };

            axios.post(`/data-import/${vm.importId}/archive-household?${params}`, null, config)
                .then(function (response) {
                    if (response.status === 200) {
                        vm.householdStats.archived =
                            archived ? vm.householdStats.archived + 1 : Math.max(0, vm.householdStats.archived - 1);
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
        queueImportMerge() {
            let vm = this;
            if (vm.householdStats.noHead > 0) {
                const words = vm.plural([['is', 'are'], ['household'], ["doesn't", "don't"]], vm.householdStats.noHead);
                vm.errorDialog(`There ${words[0]} still ${vm.householdStats.noHead} ${words[1]} that ${words[2]} have a household head.`);
            } else {
                vm.confirm(
                    '<p>Proceed merging data?</p><br /><p>This action cannot be undone</p>',
                    function () {
                        const errorMessage = 'Merging cannot be done at the momemnt. Please try again';
                        const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'] } };

                        vm.isLoading = true;

                        axios.post(`/data-import/${vm.importId}/${vm.importSource}/merge`, null, config)
                            .then(function (response) {
                                if (response.status == 200) {
                                    vm.msgDialog('Merging has been queued and will be done in the background.', '', 'success', 'check')
                                    window.location.href = '/data-import';
                                } else {
                                    vm.isLoading = false;
                                    vm.errorDialog(errorMessage);
                                }
                            })
                            .catch(function (error) {
                                vm.isLoading = false;
                                vm.errorDialog(errorMessage);
                            });
                    },
                    () => vm.snackbar('Merging cancelled', 'info'),
                    'Confirm merging data',
                    'danger'
                )
            }
        },
        plural(words, count) {
            res = [];
            for (v in words) {
                w = words[v];
                if (count != 1) {
                    res.push(w.length > 1 ? w[1] : w[0] + 's');
                } else {
                    res.push(w[0]);
                }
            }
            return res;
        },
        mlCode(code) {
            return code && `ML-${code}`;
        },
        memberDetails(row) {
            let vm = this;

            vm.isLoading = true;

            axios.get(`/data-import/${vm.importId}/household-members?household-id=${row.householdId}`)
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
                .then(function () {
                    vm.isLoading = false
                });
        },
        setHouseholdHead(row, household) {
            let vm = this;
            vm.isLoading = true;

            const params = [
                `household-id=${row.householdId}`,
                `member-id=${row.householdMemberId}`
            ].join('&');

            const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'] } };

            axios.post(`/data-import/${vm.importId}/update-household-head?${params}`, null, config)
                .then(function (response) {
                    if (response.status === 200) {
                        vm.snackbar('Updated household succesfully', 'success');
                        household.householdHeadName = row.name;
                        household.hasHouseholdHead = true;
                        vm.householdStats.noHead = Math.max(0, vm.householdStats.noHead - 1);
                        // TODO copy the other properties if/when needed
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
        getHouseholdCountWithoutHead() {
            let vm = this;
            let error_message = 'Error getting count of households without heads'
            vm.isLoading = true;

            axios.get(`/data-import/${vm.importId}/household-stats`)
                .then(function (response) {
                    if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
                        vm.householdStats = response.data;
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
        archiveStatusChanged(hhid, status) {
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
