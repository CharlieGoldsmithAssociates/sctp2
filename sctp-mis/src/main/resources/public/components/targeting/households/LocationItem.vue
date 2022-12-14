<template>
    <li>
        <a @click="toggleState">
            <span class="icon">
                <i :class="iconClass"></i>
            </span>
            {{ title }} ({{ householdCount }})
        </a>
        <ul v-if="expanded">
            <li class="r">
                <a class="clickable" v-if="!this.isLoading" @click="getHouseholdLocations">
                    <span class="icon is-small"><i
                            class="fas fa-redo"></i></span>
                    Refresh
                </a>
            </li>
            <!-- <location-item v-for="location in locations" :key="location.id" :title="location.name"
                :location-level="location.locationType" /> -->
        </ul>
    </li>
</template>

<script>
module.exports = {
    props: {
        title: {
            type: String,
            required: false
        },
        locationLevel: {
            type: Number,
            required: true
        }
    },
    components: {
       /*  'location-item': httpVueLoader('/components/targeting/households/LocationItem.vue') */
    },
    data() {
        return {
            householdCount: 9400,
            expanded: false,
            fetchCount: 0,
            isLoading: false,
            locations: [{id: 1006, name:'Chauma', locationType: 4, householdCount: 10}]
        }
    },
    computed: {
        iconClass() {
            return 'fas fa-angle-' + (this.expanded ? 'down' : 'right');
        }
    },
    methods: {
        toggleState() {
            let vm = this;
            if (vm.expanded) {
                vm.expanded = false;
            } else {
                vm.expanded = true;
            }
            if (vm.fetchCount == 0) {
                vm.getHouseholdLocations();
            }
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
.clickable {
    display: inline-block !important;
    font-size: 11px;
    margin: 0px !important;
    padding: 0px !important;
}

.clickable:hover {
    background-color: transparent !important;
    text-decoration: underline !important;
    border: none;
    color: red !important;
}

.r {
    padding-left: 16px;
    margin-top: -16px !important;
    padding-top: 0px !important;
    padding-bottom: 0px !important;
}
</style>