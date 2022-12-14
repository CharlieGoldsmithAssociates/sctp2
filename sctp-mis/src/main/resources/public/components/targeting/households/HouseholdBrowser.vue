<template>

    <div class="menu">
        <p class="menu-label has-text-primary">
            Showing households by locations
        </p>
        <ul class="menu-list">
            <location-item :title="location.name" :location-level="location.locationType"
                v-for="(location, idx) in locations" v-bind:key="idx" />
        </ul>
    </div>
</template>

<script>

const TYPE_DISTRICT = 1;
const TYPE_TA = 2;
const TYPE_CLUSTER = 3;
const TYPE_ZONE = 4;
const TYPE_VILLAGE = 5;


module.exports = {
    props: {

    },
    data() {
        return {
            locations: [{ id: 1002, name: 'Dedza', locationType: 1 }],
            isLoading: false,
        }
    },
    components: {
        LocationItem: httpVueLoader('/components/targeting/households/LocationItem.vue')
    },
    mounted() {
        this.getHouseholdLocations();
    },
    computed: {

    },
    methods: {
        checkImportStatus() {
            let vm = this;
            vm.isLoading = true;
            axios.get('/locations/get-import-status')
                .then(function (response) {
                    if (response.status == 200) {
                        if (isJsonContentType(response.headers['content-type'])) {
                            vm.statusData = response.data.status;
                        } else {
                            throw 'invalid type';
                        }
                    } else {
                        throw `Server returned: ${response.status}`;
                    }
                })
                .catch(function (error) {
                    vm.errorDialog('There was an error connecting to the server. Please try again');
                    console.log(error);
                })
                .then(function () {
                    vm.isLoading = false;
                });
        },
        getHouseholdLocations() {
            let vm = this;
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
