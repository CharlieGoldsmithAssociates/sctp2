<template>
    <div class="card no-overlap">
        <header class="card-header">
            <p class="card-header-title">Household Browser</p>
        </header>
        <div class="card-content">
            <section>
                <splitpanes>
                    <pane min-size="1">
                        <list-box v-bind:auto-load="true" @selected="onItemSelected" location-type="DISTRICT"
                            title="District"></list-box>
                    </pane>
                    <pane min-size="1">
                        <list-box :parent-code="districtCode" @selected="onItemSelected" location-type="TA"
                            title="T/A" />
                    </pane>
                    <pane>
                        <splitpanes horizontal="horizontal">
                            <pane min-size="1">
                                <splitpanes>
                                    <pane min-size="1">
                                        <list-box :parent-code="taCode" @selected="onItemSelected"
                                            location-type="CLUSTER" title="Cluster" list-size="half-height"></list-box>
                                    </pane>
                                    <pane min-size="1">
                                        <list-box :parent-code="clusterCode" @selected="onItemSelected"
                                            location-type="ZONE" title="Zone" list-size="half-height"></list-box>
                                    </pane>
                                </splitpanes>
                            </pane>
                            <pane min-size="1">
                                <list-box :parent-code="taCode" :auto-select="false" @selected="onItemSelected"
                                    location-type="GVH" title="or Group Village Head"
                                    list-size="half-height"></list-box>
                            </pane>
                        </splitpanes>
                    </pane>
                    <pane min-size="1">
                        <list-box :parent-code="villageParentCode" :use-gvh="useGvh" @selected="onItemSelected"
                            location-type="VILLAGE" title="Village"></list-box>
                    </pane>
                </splitpanes>
                <splitpanes>
                    <pane min-size="1">
                        <household-list :village-code="villageCode" />
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
            districtCode: 0,
            taCode: 0,
            clusterCode: 0,
            villageParentCode: 0,
            villageCode: 0,
            useGvh: false
        }
    },
    components: {
        Splitpanes,
        Pane,
        'ListBox': httpVueLoader('/components/targeting/households/ListBox.vue'),
        'HouseholdList': httpVueLoader('/components/targeting/households/HouseholdList.vue')
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
                case 'TA':
                    this.taCode = item.code;
                    break;
                case 'CLUSTER':
                    this.clusterCode = item.code;
                    break;
                case 'ZONE':
                    this.villageParentCode = item.code;
                    this.useGvh = false;
                    break;
                case 'GVH':
                    this.villageParentCode = item.code;
                    this.useGvh = true;
                    break;
                case 'VILLAGE':
                    this.villageCode = item.code;
                    break;
            }
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