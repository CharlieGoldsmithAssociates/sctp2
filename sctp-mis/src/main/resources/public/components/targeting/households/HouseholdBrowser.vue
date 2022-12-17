<template>
    <div class="card no-overlap">
        <header class="card-header">
            <p class="card-header-title">Household Browser</p>
        </header>
        <div class="card-content">
            <section>
                <splitpanes>
                    <pane min-size="1">
                        <list-box v-bind:auto-load="true" v-on:item-selected="this.onItemSelected"
                            location-type="DISTRICT" title="District"></list-box>
                    </pane>
                    <pane min-size="1">
                        <list-box :parent-code="districtCode" location-type="TA" title="T/A" />
                    </pane>
                    <pane>
                        <splitpanes horizontal="horizontal">
                            <pane min-size="1">
                                <splitpanes>
                                    <pane min-size="1">
                                        <list-box location-type="CLUSTER" title="Cluster"
                                            list-size="half-height"></list-box>
                                    </pane>
                                    <pane min-size="1">
                                        <list-box location-type="ZONE" title="Zone" list-size="half-height"></list-box>
                                    </pane>
                                </splitpanes>
                            </pane>
                            <pane min-size="1">
                                <list-box location-type="GVH" title="or Group Village Head"
                                    list-size="half-height"></list-box>
                            </pane>
                        </splitpanes>
                    </pane>
                    <pane min-size="1">
                        <list-box location-type="VILLAGE" title="Village"></list-box>
                    </pane>
                </splitpanes>
            </section>
        </div>
    </div>
</template>

<script>

const { Splitpanes, Pane } = splitpanes;

module.exports = {
    props: {

    },
    data() {
        return {
            isLoading: false,
            districtCode: 0
        }
    },
    components: {
        Splitpanes,
        Pane,
        'ListBox': httpVueLoader('/components/targeting/households/ListBox.vue')
    },
    mounted() {

    },
    computed: {

    },
    methods: {
        onItemSelected(item, locationType) {
            switch (locationType) {
                case 'DISTRICT':
                    this.districtCode = item.code;
                    break;

            }
            alert(item);
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