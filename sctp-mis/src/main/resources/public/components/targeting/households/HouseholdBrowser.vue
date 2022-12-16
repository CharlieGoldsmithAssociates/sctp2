<template>
    <div class="card no-overlap">
        <header class="card-header">
            <p class="card-header-title">Household Browser</p>
        </header>
        <div class="card-content">
            <section>
                <splitpanes>
                    <pane min-size="1">
                        <list-box title="District"></list-box>
                    </pane>
                    <pane min-size="1">
                        <list-box title="T/A" />
                    </pane>
                    <pane>
                        <splitpanes horizontal="horizontal">
                            <pane min-size="1">
                                <splitpanes>
                                    <pane min-size="1">
                                        <list-box title="Cluster" list-size="half-height"></list-box>
                                    </pane>
                                    <pane min-size="1">
                                        <list-box title="Zone" list-size="half-height"></list-box>
                                    </pane>
                                </splitpanes>
                            </pane>
                            <pane min-size="1">
                                <list-box title="or Group Village Head" list-size="half-height"></list-box>
                            </pane>
                        </splitpanes>
                    </pane>
                    <pane min-size="1">
                        <list-box title="Village"></list-box>
                    </pane>
                </splitpanes>
            </section>
        </div>
    </div>
</template>

<script>

const TYPE_DISTRICT = 1;
const TYPE_TA = 2;
const TYPE_CLUSTER = 3;
const TYPE_ZONE = 4;
const TYPE_VILLAGE = 5;

const { Splitpanes, Pane } = splitpanes;

module.exports = {
    props: {

    },
    data() {
        return {
            isLoading: false,
        }
    },
    components: {
        Splitpanes,
        Pane,
        'ListBox': httpVueLoader('/components/targeting/households/ListBox.vue')
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

<style scoped>
.bordered {
    border: 1px solid #eee;
    border-radius: 5px;
}
</style>