<template>
  <section>
    <div id="alt-receiver" class="content-tab">
      <b-message type="is-info">
        Specify the details of the secondary/alternate receiver for this
        household.
      </b-message>

      <div class="columns mt-3">
        <div class="column">
          <div class="columns is-vcentered">
            <div class="column">
              <b-image
                id="main-photo"
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
              <Members-Dropdown :household-id="householdId" />
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
                  :icon-right="selected ? 'close-circle' : ''"
                  icon-right-clickable
                  @icon-right-click="clearDate"
                  trap-focus
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
          <div class="field mt-4">
            <label class="label">Update recipient photo (5 MB max)</label>
            <div class="control is-expanded">
              <b-image
                id="altPreviewPhoto"
                src="/assets/img/user-svg.svg"
                class="preview-image obj-fit-contain"
                :rounded="roundedImage"
                alt="Preview photo"
              ></b-image>
            </div>
          </div>
          <div class="columns mt-6">
            <div class="column">
              <button
                id="alt-modal-btn"
                class="button is-primary modal-button"
                data-target="modal"
                aria-haspopup="true"
              >
                Take picture
              </button>
            </div>
            <div class="column">
              <div class="file">
                <label class="file-label">
                  <input
                    class="file-input"
                    type="file"
                    accept="image/*"
                    name="alt-photo"
                    onchange="loadAltFile(event)"
                  />
                  <span class="file-cta">
                    <span class="file-icon">
                      <i class="fas fa-upload"></i>
                    </span>
                    <span class="file-label"> Choose a file… </span>
                  </span>
                </label>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="buttons is-right mt-5">
        <button
          id="altReceiverLoad"
          class="button is-info"
          @click="loadAltReceiver()"
        >
          Reload
        </button>
        <button
          id="altReceiverSend"
          class="button is-primary"
          @click="saveAltReceiver()"
        >
          Save
        </button>
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
      isHouseholdMember: true,
    };
  },
  components: {
    'MembersDropdown': httpVueLoader("/components/MembersDropdown.vue"),
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
    saveAltReceiver() {},
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
    clearDate() {
      this.dateOfBirth = null;
    },
  },
};
</script>
