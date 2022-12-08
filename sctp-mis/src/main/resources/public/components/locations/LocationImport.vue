<template>
    <section>

        <b-message type="is-info">
            <p>Click the <strong>Synchronize Locations</strong> button to download locations from the UBR.</p>
            <br />
            <p>Note that the import process runs in the background and only one process can be run at a time.</p>
        </b-message>

        <h4>Location Download History</h4>
        <hr style="margin: 0" />

        <div class="import-details-wrapper">
            <table class="import-details">
                <tr>
                    <th>Start Time</th>
                    <td>{{                                                                                                        (statusData && new Date(statusData.start_date).toString())                                                                                                        }}</td>
                </tr>
                <tr>
                    <th>End Time</th>
                    <td>{{                                                                                                        (statusData && (statusData.end_date && new Date(statusData.end_date).toString())) }}</td>
                </tr>
                <tr>
                    <th>Status</th>
                    <td><b-tag :type="lastSessionStatus">{{                                                                                                        (statusData && statusData.status)                                                                                                        }}</b-tag></td>
                </tr>
                <tr>
                    <th>Locations</th>
                    <td>{{ (statusData && statusData.count) }}</td>
                </tr>
                <tr>
                    <th>Downloaded By</th>
                    <td>{{ (statusData && statusData.created_by) }}</td>
                </tr>
            </table>
        </div>

        <nav class="level" style="margin-top: 1rem">
            <!-- Right side -->
            <div class="level-right">
                <div class="level-item">
                    <div class="buttons">
                        <b-button :loading="isLoading" icon-left="refresh" @click="checkImportStatus"
                            type="is-primary">CheckStatus</b-button>
                        <b-button @click="queueLocationImportTask" :disabled="isDownloading" icon-left="cloud-sync"
                            :loading="isLoading" type="is-success">Synchronize Locations</b-button>
                    </div>
                </div>
            </div>
        </nav>
    </section>
</template>

<script>

module.exports = {
    props: {

    },
    data() {
        return {
            statusData: null,
            isLoading: false,
            total: 0
        }
    },
    mounted() {
        this.checkImportStatus();
    },
    computed: {
        isDownloading() {
            return this.statusData !== null && this.statusData.status === 'Downloading';
        },
        lastSessionStatus() {
            var classList = ['is-light'];
            if (this.statusData !== null) {
                switch (this.statusData.status) {
                    case 'Downloading':
                        classList.push('is-info');
                        break;
                    case 'Error':
                        classList.push('is-danger');
                        break;
                    case 'Downloaded':
                        classList.push('is-success');
                        break;
                }
            }
            return classList.join(' ');
        }
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
        queueLocationImportTask() {
            let vm = this;
            vm.confirm(
                'Import locations from UBR?',
                function () {
                    const errorMessage = 'Location import cannot be done at the momemnt. Please try again';
                    const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'] } };

                    vm.isLoading = true;

                    axios.post(`/locations/queue-imports-task`, null, config)
                        .then(function (response) {
                            if (response.status == 200) {
                                vm.statusData = response.data.status;
                                vm.msgDialog('Location import request is processing. You may proceed with other tasks', '', 'success', 'check')
                            } else if (response.status == 406) {
                                vm.msgDialog('Another location import process is already running', '', 'warn', 'warn')
                            } else {
                                vm.isLoading = false;
                                vm.errorDialog(errorMessage);
                            }
                        })
                        .catch(function (error) {
                            vm.isLoading = false;
                            vm.errorDialog(errorMessage);
                        }).then(function () {
                            vm.isLoading = false;
                        });;
                },
                () => vm.snackbar('Location import cancelled', 'info'),
                'Confirm importing locations',
                'danger'
            )
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
.import-details tr th,
.import-details tr td {
    padding: 2px 16px 2px 16px;
    margin: 0px;
    border: 0px;
    border-bottom: 1px solid #dedede;
}

.import-details-wrapper {
    padding: 1rem;
}
</style>