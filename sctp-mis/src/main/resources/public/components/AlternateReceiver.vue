<template>
  <section>
    <b-message type="is-info">
      Specify the details of the secondary/alternate receiver for this
      household.
    </b-message>

    <div class="columns mt-3">
      <div class="column">
        <div class="columns is-vcentered">
          <div class="column">
            <b-image
              id="alt-photo"
              :src="`/targeting/enrolment/recipient-photo?household=${householdId}&amp;type=secondary`"
              src-fallback="/assets/img/user-svg.svg"
              class="p4p obj-fit-contain"
              :rounded="roundedImage"
              alt="Alternate receiver photo"
            ></b-image>
          </div>

          <div class="column">
            <div class="info-list">
              <div class="info-row">
                <div class="item-label">Full Name</div>
                <div class="item-value">
                  <span v-if="data.firstName || data.lastName">
                    {{ data.firstName }} {{ data.lastName }}
                  </span>
                  <span v-else class="has-text-danger-dark">
                    Not available
                  </span>
                </div>
              </div>
              <div class="info-row">
                <div class="item-label">Gender</div>
                <div class="item-value">
                  <span v-if="data.gender">{{ data.gender }} </span>
                  <span v-else class="has-text-danger-dark">
                    Not available
                  </span>
                </div>
              </div>
              <div class="info-row">
                <div class="item-label">Date of birth</div>
                <div class="item-value">
                  <span v-if="data.dateOfBirth">{{ data.dateOfBirth }} </span>
                  <span v-else class="has-text-danger-dark">
                    Not available
                  </span>
                </div>
              </div>
              <div class="info-row">
                <div class="item-label">National Id</div>
                <div class="item-value">
                  <span v-if="data.individualId">
                    {{ data.individualId }}
                  </span>
                  <span v-else class="has-text-danger-dark">
                    Not available
                  </span>
                </div>
              </div>
              <div class="info-row" title="Is a Household Member">
                <div class="item-label">Is a HH Member</div>
                <div class="item-value">
                  <span v-if="data.isHouseholdMember"> Yes </span>
                  <span v-else> No </span>
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
        <!-- form starts here -->
        <form id="altReceiverForm" enctype="multipart/form-data" method="POST">
          <div class="control mb-5">
            <label class="radio">
              <input
                id="householdMemberBtn"
                type="radio"
                v-model="isHouseholdMember"
                :value="true"
                :checked="isHouseholdMember"
              />
              Household Member
            </label>
            <label class="radio">
              <input
                id="nonHouseholdMemberBtn"
                type="radio"
                v-model="isHouseholdMember"
                :value="false"
                :checked="isHouseholdMember"
              />
              Non-Household Member
            </label>
          </div>
          <div id="altMember" class="columns" v-if="isHouseholdMember">
            <div class="column is-4">
              <label class="label">Member</label>
            </div>
            <div class="column">
              <Members-Dropdown
                :household-id="householdId"
                v-model="memberId"
                ref="recipients"
              />
            </div>
          </div>
          <div class="columns" v-if="!isHouseholdMember">
            <div class="column is-4">
              <label class="label">First Name</label>
            </div>
            <div class="column">
              <b-field>
                <b-input v-model="firstName" type="text" required> </b-input>
              </b-field>
            </div>
          </div>
          <div class="columns" v-if="!isHouseholdMember">
            <div class="column is-4">
              <label class="label">Last Name</label>
            </div>
            <div class="column">
              <b-field>
                <b-input v-model="lastName" type="text" expanded required>
                </b-input>
              </b-field>
            </div>
          </div>
          <div class="columns" v-if="!isHouseholdMember">
            <div class="column is-4">
              <label class="label">Gender</label>
            </div>
            <div class="column">
              <b-select
                v-model="gender"
                placeholder="Select gender"
                required
                expanded
              >
                <option value="Male">Male</option>
                <option value="Female">Female</option>
              </b-select>
            </div>
          </div>
          <div class="columns" v-if="!isHouseholdMember">
            <div class="column is-4">
              <label class="label">Date of Birth</label>
            </div>
            <div class="column">
              <b-field>
                <b-datepicker
                  v-model="dateOfBirth"
                  placeholder="Click to select..."
                  icon="calendar-today"
                  :icon-right="dateSelected ? 'close-circle' : ''"
                  icon-right-clickable
                  @icon-right-click="clearDateOfBirth"
                  trap-focus
                  required
                >
                </b-datepicker>
              </b-field>
            </div>
          </div>
          <div class="columns" v-if="!isHouseholdMember">
            <div class="column is-4">
              <label class="label">National ID</label>
            </div>
            <div class="column">
              <b-field>
                <b-input v-model="mlCode" type="text" expanded required>
                </b-input>
              </b-field>
            </div>
          </div>
          <div class="columns" v-if="!isHouseholdMember">
            <div class="column is-4">
              <label class="label">ID Issue Date</label>
            </div>
            <div class="column">
              <b-field>
                <b-datepicker
                  v-model="idIssueDate"
                  placeholder="Click to select..."
                  icon="calendar-today"
                  :icon-right="dateSelected ? 'close-circle' : ''"
                  icon-right-clickable
                  @icon-right-click="clearIssueDate"
                  trap-focus
                >
                </b-datepicker>
              </b-field>
            </div>
          </div>
          <div class="columns" v-if="!isHouseholdMember">
            <div class="column is-4">
              <label class="label">ID Expiry Date</label>
            </div>
            <div class="column">
              <b-field>
                <b-datepicker
                  v-model="idExpiryDate"
                  placeholder="Click to select..."
                  icon="calendar-today"
                  :icon-right="dateSelected ? 'close-circle' : ''"
                  icon-right-clickable
                  @icon-right-click="clearExpiryDate"
                  trap-focus
                >
                </b-datepicker>
              </b-field>
            </div>
          </div>
          <div class="field mt-4">
            <label class="label">Update recipient photo (5 MB max)</label>
            <div class="control is-expanded">
              <img
                id="altPreviewPhoto"
                class="preview-image obj-fit-contain is-rounded"
                src="/assets/img/user-svg.svg"
                alt="Preview image"
              />
            </div>
          </div>
          <!---->
          <div class="columns">
            <div class="column mt-6">
              <div class="level">
                <div class="level-left">
                  <div class="level-item">
                    <div class="file is-text is-small">
                      <label class="file-label">
                        <input
                          class="file-input"
                          id="altPhotoSelection"
                          required="required"
                          type="file"
                          onchange="if(this.files[0]){ altPreviewPhoto.src = window.URL.createObjectURL(this.files[0]); }"
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
                      onclick="altPreviewPhoto.src = '/assets/img/user-svg.svg'; altPhotoSelection.value='';"
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
                      @click="saveAltReceiver"
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
        <!-- form ends here -->
      </div>
    </div>

    <div class="divider is-horizontal"></div>

    <div class="columns">
      <div class="column">
        <b-button type="is-info" @click="getAlternateRecipient">
          Reload alternate recipient details
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
      isLoading: false,
      individuals: [],
      roundedImage: true,
      memberId: null,
      firstName: null,
      lastName: null,
      gender: null,
      dateOfBirth: null,
      individualId: null,
      mlCode: null,
      dateSelected: true,
      isHouseholdMember: true,
      idExpiryDate: null,
      idIssueDate: null,
    };
  },
  components: {
    MembersDropdown: httpVueLoader("/components/MembersDropdown.vue"),
  },
  mounted() {
    this.getAlternateRecipient();
  },
  computed: {},
  methods: {
    getAlternateRecipient() {
      let vm = this;
      vm.isLoading = true;
      const params = [`household=${vm.householdId}`].join("&");
      axios
        .get(`/targeting/enrolment/secondary-recipient?${params}`)
        .then(function (response) {
          if (response.status == 200) {
            var hasData = response.data;

            if (hasData) {
              if (isJsonContentType(response.headers["content-type"])) {
                vm.data = response.data;
                console.log("Alternate RECIEVER", vm.data);
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
            "There was an error loading alternate recipient. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    saveAltReceiver() {
      let vm = this;
      vm.isLoading = true;
      var photo = document.querySelector("#altPhotoSelection").files[0];
      fData = new FormData();
      fData.append("household", vm.householdId);
      fData.append("session", vm.sessionId);
      fData.append("photo", photo);
      var altType = "";
      if (vm.isHouseholdMember) {
        fData.append("id", vm.memberId);
        altType = "member";
      } else {
        altType = "other";
        fData.append("firstName", vm.firstName);
        fData.append("lastName", vm.lastName);
        fData.append("dateOfBirth", vm.convertDate(vm.dateOfBirth));
        fData.append("nationalId", vm.mlCode);
        fData.append("gender", vm.gender);
        fData.append("idIssueDate", vm.convertDate(vm.idIssueDate));
        fData.append("idExpiryDate", vm.convertDate(vm.idExpiryDate));
      }
      fData.append("altType", altType);

      for (var pair of fData.entries()) {
        console.log(pair[0] + ", " + pair[1]);
      }

      const params = [`type=secondary`].join("&");
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
            vm.reloadRecipientImage();
            vm.getAlternateRecipient();
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
    reloadRecipientImage() {},
    handleClick() {},
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
    clearDateOfBirth() {
      this.dateOfBirth = null;
    },
    clearIssueDate() {
      this.idIssueDate = null;
    },
    clearExpiryDate() {
      this.idExpiryDate = null;
    },
    convertDate(date) {
      const tzOffset = date.getTimezoneOffset() * 60 * 1000;
      return new Date(date - tzOffset).toISOString().split("T")[0];
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
