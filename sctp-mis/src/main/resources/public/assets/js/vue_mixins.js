/**
 * Mixin for SCTP Vue2 + Bulma
 */

Vue.mixin({
  methods: {
    showErrorDialog(msg, titleText = 'Error') {
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

    showMessage(msg, titleText = '', dlgType = 'info', icon = '') {
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
    },

    showSnackbar(msg, msgType = 'info') {
      this.$buefy.toast.open({
        duration: 5000,
        message: msg,
        position: 'is-bottom',
        type: 'is-' + msgType
      })
    },
  },
})