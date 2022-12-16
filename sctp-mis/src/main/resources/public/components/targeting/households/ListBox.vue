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
                    <li v-for="(item, idx) in items" :key="idx">
                        <a :class="item.selected ? 'is-active' : ''" @click="onListItemClicked(item)">{{
                                item.label
                        }}<br />
                            <span class="location-code">{{ item.subtitle }}</span>
                        </a>
                    </li>
                </ul>
            </aside>
        </div>
        <b-loading :is-full-page="true" v-model="isLoading" :can-cancel="false"></b-loading>
    </div>
</template>

<script>
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
        }
    },
    data() {
        return {
            searchTerm: '',
            expanded: false,
            fetchCount: 0,
            isLoading: false,
            selectedItem: null,
            items: [
                { code: 1006, label: 'Item1 (100)', subtitle: 'Subttitle', selected: false },
                { code: 1007, label: 'Item2 (100)', subtitle: 'Subttitle', selected: false },
                { code: 1008, label: 'Item3 (100)', subtitle: 'Subttitle', selected: false },
                { code: 1009, label: 'Item4 (100)', subtitle: 'Subttitle', selected: false },
                { code: 1010, label: 'Item5 (100)', subtitle: 'Subttitle', selected: false },
            ]
        }
    },
    computed: {
        currentCode() {
            return this.selectedCode;
        },
        iconClass() {
            return '';
        }
    },
    mounted() {

    },
    methods: {
        updateSelection(newItem) {
            if (this.selectedItem != newItem) {
                if (this.selectedItem !== null) {
                    this.selectedItem.selected = false;
                }
                (this.selectedItem = newItem).selected = true;
            }
        },
        onListItemClicked(item) {
            let vm = this;
            vm.isLoading = true;
            setTimeout(() => { vm.isLoading = false; vm.updateSelection(item); }, 3000);
        },
        getHouseholdLocations() {
            let vm = this;
            vm.isLoading = true;
            vm.isLoading = false;
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
</style>