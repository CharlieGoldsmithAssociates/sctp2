<template>
  <section>
    <b-message type="is-info">
      Specify the details of the primary receiver for this household
    </b-message>

    <div class="columns mt-3">
      <!-- {% verbatim %} -->
      <div class="column">
        <div class="columns is-vcentered">
          <div class="column">
            <b-image
              id="main-photo"
              :src="mainRecipientImageUrl"
              src-fallback="/assets/img/user-svg.svg"
              class="p4p obj-fit-contain"
              :rounded="roundedImage"
              alt="Main receiver photo"
            ></b-image>
          </div>

          <div class="column">
            <div class="info-list">
              <div class="info-row">
                <div class="item-label">Household member</div>
                <div class="item-value">
                  <span v-if="data.firstName || data.lastName"
                    >{{ data.firstName }} {{ data.lastName }}</span
                  >
                  <span v-else class="has-text-danger-dark">Not available</span>
                </div>
              </div>
              <div class="info-row">
                <div class="item-label">Gender</div>
                <div class="item-value">
                  <span v-if="data.gender">{{ data.gender }}</span>
                  <span v-else class="has-text-danger-dark">Not available</span>
                </div>
              </div>
              <div class="info-row">
                <div class="item-label">Date of birth</div>
                <div class="item-value">
                  <span v-if="data.dateOfBirth">{{ data.dateOfBirth }}</span>
                  <span v-else class="has-text-danger-dark">Not available</span>
                </div>
              </div>
              <div class="info-row">
                <div class="item-label">National Id</div>
                <div class="item-value">
                  <span v-if="data.individualId">{{ data.individualId }}</span>
                  <span v-else class="has-text-danger-dark">Not available</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="columns">
        <div class="divider is-vertical">UPDATE</div>
      </div>

      <div class="column">
        <form id="mainReceiverForm" enctype="multipart/form-data" method="POST">
          <div class="column is-4">
            <label class="label">Select new main recipient</label>
          </div>
          <div class="column">
            <Members-Dropdown
              :household-id="householdId"
              v-model="memberId"
              ref="recipients"
            />
          </div>

          <div class="field">
            <label class="label">Update recipient photo (5 MB max)</label>
            <div class="control is-expanded">
              <img
                id="mainPreviewPhoto"
                class="preview-image obj-fit-contain is-rounded"
                src="/assets/img/user-svg.svg"
                alt="Preview image"
              />
            </div>
          </div>

          <div class="columns">
            <div class="column mt-6">
              <div class="level">
                <div class="level-left">
                  <div class="level-item">
                    <div class="file is-text is-small">
                      <label class="file-label">
                        <input
                          class="file-input"
                          id="mainPhotoSelection"
                          required="required"
                          type="file"
                          onchange="if(this.files[0]){ mainPreviewPhoto.src = window.URL.createObjectURL(this.files[0]); }"
                          maxlength="5242880"
                          accept="image/jpeg; image/png"
                          name="photo"
                        />
                        <span class="file-cta">
                          <span class="file-icon">
                            <i class="fas fa-upload"></i>
                          </span>
                          <span class="file-label"> Select photo </span>
                        </span>
                      </label>
                    </div>
                  </div>
                  <div class="level-item">
                    <button
                      onclick="mainPreviewPhoto.src = '/assets/img/user-svg.svg'; mainPhotoSelection.value='';"
                      class="button is-inverted is-small is-text is-danger"
                    >
                      Cancel photo selection
                    </button>
                  </div>
                </div>
                <div class="level-right">
                  <div class="level-item">
                    <button
                      class="button is-small is-link"
                      @click="saveMainReceiver"
                      type="button"
                    >
                      Save changes
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div class="divider is-horizontal"></div>

    <div class="columns">
      <div class="column">
        <b-button type="is-info" @click="getMainRecipient">
          Reload main recipient details
        </b-button>
      </div>
      <div class="column has-text-right">
        <b-button
          type="is-info is-link"
          @click="$refs.recipients.getRecipientCandidates()"
        >
          Refresh members list
        </b-button>
      </div>
    </div>
  </section>
</template>

<script>
module.exports = {
  props: {
    householdId: {
      type: Number,
      required: true,
    },
    sessionId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      data: "",
      isLoading: true,
      candidates: [],
      roundedImage: true,
      memberId: null,
      baseUrl: `/targeting/enrolment/recipient-photo?household=${this.householdId}&type=primary`,
      imageUrl: null,
    };
  },
  mounted() {
    this.getMainRecipient();
    this.reloadRecipientImage();
  },
  components: {
    MembersDropdown: httpVueLoader("/components/MembersDropdown.vue"),
  },
  computed: {
    mainRecipientImageUrl: function () {
      return this.imageUrl;
    },
  },
  methods: {
    reloadRecipientImage() {
      var ts = new Date().getTime();
      this.imageUrl = this.baseUrl + `&cache-buster=${ts}`;
    },
    getMainRecipient() {
      let vm = this;
      vm.isLoading = true;
      const params = [`household=${vm.householdId}`].join("&");
      axios
        .get(`/targeting/enrolment/primary-recipient?${params}`)
        .then(function (response) {
          if (response.status == 200) {
            var hasData = response.data;
            
            if (hasData) {
              if (isJsonContentType(response.headers["content-type"])) {
                vm.data = response.data;
                vm.reloadRecipientImage();
              } else {
                throw "invalid type";
              }
            } else {
              vm.snackbar("No more data to load");
            }
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading main recipient. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    saveMainReceiver() {
      let vm = this;
      vm.isLoading = true;
      var photo = document.querySelector("#mainPhotoSelection").files[0];
      fData = new FormData();
      fData.append("id", vm.memberId);
      fData.append("household", vm.householdId);
      fData.append("session", vm.sessionId);
      fData.append("photo", photo);

      const params = [`type=primary`].join("&");
      const config = {
        headers: {
          "X-CSRF-TOKEN": csrf()["token"],
          "Content-Type": "multipart/form-data",
        },
      };
      axios
        .post(`/targeting/enrolment/update-recipient?${params}`, fData, config)
        .then(function (response) {
          if (response.status === 200) {
            vm.getMainRecipient();
            vm.reloadRecipientImage();
            vm.msgDialog("Updated successfully.", "", "success", "check");
          } else {
            throw `Status: ${response.status}`;
          }
        })
        .catch(function (error) {
          console.log(error);
          vm.errorDialog("Error updating data");
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    snackbar(msg, msgType = "info") {
      this.$buefy.toast.open({
        duration: 5000,
        message: msg,
        position: "is-bottom",
        type: "is-" + msgType,
      });
    },

    errorDialog(msg, titleText = "Error") {
      this.$buefy.dialog.alert({
        title: titleText,
        message: msg,
        type: "is-danger",
        hasIcon: true,
        icon: "times-circle",
        iconPack: "fa",
        ariaRole: "alertdialog",
        ariaModal: true,
      });
    },
    confirm(
      msg,
      yesfn,
      nofn = null,
      title = null,
      type = "primary",
      hasIcon = true
    ) {
      this.$buefy.dialog.confirm({
        message: msg,
        title: title,
        hasIcon: hasIcon,
        type: "is-" + type,
        onConfirm: () => yesfn(),
        onCancel: () => nofn && nofn(),
      });
    },
    msgDialog(msg, titleText = "", dlgType = "info", icon = "") {
      this.$buefy.dialog.alert({
        title: titleText,
        message: msg,
        type: "is-" + dlgType,
        hasIcon: icon !== "",
        icon: icon,
        iconPack: "fa",
        ariaRole: "alertdialog",
        ariaModal: true,
      });
    },
  },
};
</script>

<style scoped>
.receiver-image {
  width: 512px !important;
  height: 412px !important;
}

.preview-image {
  width: 250px !important;
  height: 250px !important;
}

.htmx-indicator {
  display: none;
}
</style>
