<template>
    <div :class="`column is-${this.size}`">
        <b-field :label="title" class="is-full-width">
            <div class="control">
                <b-input small="true" placeholder="search by code or name" :loading="isLoading" :disabled="isLoading"
                    v-model="searchTerm"></b-input>
            </div>
        </b-field>

        <div class="field is-full-width">
            <aside class="menu">
                <ul :class="`menu-list is-${this.listSize}`">
                    <li v-for="item in items" :key="item.code">
                        <a :class="selectedItemCode == item.code ? 'is-active' : ''"
                            v-on:click="() => onListItemClicked(item)">{{
                                    item.name
                            }} ({{ item.household_count }})<br />
                            <span class="location-code">{{ item.code }}</span>
                        </a>
                    </li>
                </ul>
            </aside>
        </div>
        <b-loading :is-full-page="true" v-model="isLoading" :can-cancel="false"></b-loading>
    </div>
</template>

<script>
const LOCATION_DICTIONARY = {
    DISTRICT: 'SUBNATIONAL1',
    TA: 'SUBNATIONAL2',
    CLUSTER: 'SUBNATIONAL3',
    ZONE: 'SUBNATIONAL4',
    VILLAGE: 'SUBNATIONAL5',
    GVH: 'SUBNATIONAL6'
};
module.exports = {
    props: {
        title: {
            type: String,
            required: false,
            default: ''
        },
        listSize: {
            type: String,
            default: 'full-height'
        },
        size: {
            type: String,
            default: 'full'
        },
        autoLoad: {
            type: Boolean,
            required: false,
            default: false
        },
        parentCode: {
            type: Number,
            required: false,
            default: 0
        },
        autoSelect: {
            type: Boolean,
            required: false,
            default: true
        },
        locationType: {
            type: String,
            required: true,
            validator(value) {
                return ['DISTRICT', 'TA', 'CLUSTER', 'ZONE', 'VILLAGE', 'GVH'].includes(value);
            }
        }
    },
    data() {
        return {
            searchTerm: '',
            expanded: false,
            fetchCount: 0,
            isLoading: false,
            selectedItem: null,
            items: []
        }
    },
    emits: ['selected'],
    computed: {
        selectedItemCode() {
            return this.selectedItem != null ? this.selectedItem.code : 0;
        }
    },
    mounted() {
        if (this.autoLoad) {
            this.getHouseholdLocations();
        }
    },
    watch: {
        parentCode: function (newValue, oldValue) {
            this.getHouseholdLocations();
        }
    },
    methods: {
        autoSelectFirstItem() {
            if (this.autoSelect) {
                if (this.items.length > 0) {
                    this.updateSelection(this.items[0]);
                    this.$emit('selected', this.items[0], this.locationType);
                } else {
                    this.selectedItem = null;
                }
            }
        },
        updateSelection(newItem) {
            if (this.selectedItem != newItem) {
                this.selectedItem = newItem;
            }
        },
        onListItemClicked(item) {
            this.updateSelection(item);
            this.$emit('selected', item, this.locationType);
        },
        getHouseholdLocations() {
            let vm = this;
            vm.isLoading = true;
            var parameters = [];
            var type = LOCATION_DICTIONARY[vm.locationType];

            if (vm.parentCode != null && vm.parentCode >= 1) {
                parameters.push(`parent-code=${vm.parentCode}`);
            }

            if (vm.useGvh) {
                parameters.push(`use-gvh=${vm.parentCode}`);
            }

            parameters = parameters.length > 0 ? ('?' + parameters.join('&')) : '';

            axios.get(`/locations/get-household-locations-for-browser/${type}${parameters}`)
                .then(function (response) {
                    if (response.status == 200) {
                        if (isJsonContentType(response.headers['content-type'])) {
                            vm.items = response.data;
                            vm.autoSelectFirstItem();
                        } else {
                            throw 'invalid content type';
                        }
                    } else {
                        throw `Server returned: ${response.status}`;
                    }
                })
                .catch(function (error) {
                    vm.snackbar('Error getting data', 'danger');
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

<style scoped>
.menu-list .icon.is-small {
    font-size: 12px;
    margin-top: auto;
    margin-bottom: auto;
}

.menu-list li {
    font-size: 14px;
}

.menu-list.is-full-height {
    height: 600px !important;
    overflow-y: scroll;
}

.menu-list.is-half-height {
    height: 250px !important;
    overflow-y: scroll;
}

.menu-list li a .location-code {
    font-size: 12px;
    color: #6d6d6d;
    font-weight: 500;
}

.menu-list li a.is-active .location-code {
    color: #fff;
}

.menu-list li {
    border-bottom: 1px solid #e9e9e9;
}

.column {
    border: 1px solid #dedede;
    border-radius: 5px;
}
</style>