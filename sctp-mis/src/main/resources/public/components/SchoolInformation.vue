<template>
  <section>
    <div class="content-tab">
      <b-message type="is-info">
        Add or edit information about school going children under this
        household.
      </b-message>

      <section>
        <b-table
          :data="isEmpty ? [] : schoolsEnrolled"
          :bordered="isBordered"
          :striped="isStriped"
          :narrowed="isNarrowed"
          :hoverable="isHoverable"
          :loading="isLoading"
          :focusable="isFocusable"
          :mobile-cards="hasMobileCards"
        >
          <b-table-column
            field="nameOfChild"
            label="Name of Child"
            v-slot="props"
          >
            {{ props.row.individualName }}
            &nbsp; &nbsp;
            <b-tag v-if="props.row.status" type="is-success is-small">
              Active
            </b-tag>
            <b-tag v-if="!props.row.status" type="is-danger is-small">
              Inactive
            </b-tag>
          </b-table-column>

          <b-table-column
            field="HH Member Code"
            label="HH Member Code"
            v-slot="props"
          >
            {{ props.row.householdMemberCode }}
          </b-table-column>

          <b-table-column
            field="currentSchool"
            label="Current School"
            v-slot="props"
          >
            {{ props.row.schoolName }}
          </b-table-column>

          <b-table-column label="Education Level" v-slot="props">
            {{ props.row.educationLevel }}
          </b-table-column>

          <b-table-column label="Grade" v-slot="props">
            <span> {{ props.row.grade }}</span>
          </b-table-column>

          <b-table-column label="Options" v-slot="props">
            <b-dropdown aria-role="list">
              <template #trigger="{ active }">
                <b-button
                  label="Options"
                  type="button is-info is-inverted is-options"
                  :icon-right="active ? 'menu-up' : 'menu-down'"
                />
              </template>

              <b-dropdown-item
                aria-role="listitem"
                @click="openModal(props.row, true)"
                >Edit School Info
              </b-dropdown-item>
            </b-dropdown>
          </b-table-column>

          <template #empty>
            <div class="has-text-centered">No records to display</div>
          </template>
        </b-table>
      </section>
      <div class="divider is-horizontal"></div>
      <div class="columns">
        <div class="column">
          <div class="buttons">
            <b-button
              label="New School Enrollment"
              type="is-info"
              @click="openModal"
            />
          </div>
        </div>
        <div class="column">
          <div class="buttons is-right">
            <b-button type="is-info" @click="getSchoolsEnrolled">
              Reload
            </b-button>
          </div>
        </div>
      </div>
    </div>

    <section>
      <b-modal
        v-model="isModalActive"
        scroll="keep"
        @hidden="onHidden"
        has-modal-card
        trap-focus
        :destroy-on-hide="true"
        aria-role="dialog"
        aria-label="New School Enrollment"
        close-button-aria-label="Close"
        :can-cancel="false"
      >
        <div class="card">
          <header class="modal-card-head">
            <p class="modal-card-title">New School Enrollment</p>
            <button type="button" class="delete" @click="closeModal" />
          </header>
          <div class="card-content">
            <b-message type="is-info">
              Add information about a school going child under this household.
            </b-message>
            <section>
              <b-field label="Household Member">
                <Members-Dropdown
                  :household-id="householdId"
                  :is-editing="isEditing"
                  :editing-member-id="schoolMemberId"
                  v-model="memberId"
                />
              </b-field>
              <b-field label="School">
                <b-select
                  name="schooldId"
                  placeholder="Select School"
                  required
                  expanded
                  v-model="schoolId"
                >
                  <option
                    v-for="school in allSchools"
                    :key="school.id"
                    :value="school.id"
                    :selected="schoolId == school.id"
                  >
                    {{ school.name }} - {{ school.code }} -
                    {{ school.educationZone }}
                  </option>
                </b-select>
              </b-field>
              <div class="columns">
                <div class="column">
                  <b-field label="Education Level">
                    <b-select
                      name="educationLevel"
                      placeholder="Select Education Level"
                      required
                      expanded
                      v-model="educationLevel"
                    >
                      <option
                        v-for="(level, idx) in educationLevels"
                        :key="idx"
                        :value="level"
                        :selected="educationLevel == level"
                      >
                        {{ level }}
                      </option>
                    </b-select>
                  </b-field>
                </div>
                <div class="column">
                  <b-field label="Grade Level">
                    <b-select
                      name="gradeLevel"
                      placeholder="Select Grade Level"
                      required
                      expanded
                      v-model="gradeLevel"
                    >
                      <option
                        v-for="(grdLevel, index) in gradeLevels"
                        :key="index"
                        :value="grdLevel"
                        :selected="gradeLevel == grdLevel"
                      >
                        {{ grdLevel }}
                      </option>
                    </b-select>
                  </b-field>
                </div>
              </div>
              <div class="column">
                <b-field label="Is the enrollment still active?">
                  <b-switch
                    v-model="status"
                    true-value="1"
                    false-value="0"
                  ></b-switch>
                </b-field>
              </div>
            </section>
          </div>
          <div class="modal-card-foot">
            <button class="button" type="button" @click="closeModal">
              Close
            </button>
            <button
              class="button is-success"
              type="button"
              @click="saveSchoolInfo"
            >
              Save
            </button>
          </div>
        </div>
      </b-modal>
    </section>
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
      children: [],
      schoolsEnrolled: [],
      data: [],
      isEmpty: false,
      isBordered: false,
      isStriped: true,
      isNarrowed: true,
      isHoverable: true,
      isFocusable: true,
      isLoading: false,
      hasMobileCards: true,
      children: [],
      memberId: null,
      schoolMemberId: null,
      allSchools: [],
      educationLevels: [],
      gradeLevels: [],
      status: false,
      schoolId: null,
      educationLevel: null,
      gradeLevel: null,
      isModalActive: false,
      isEditing: false,
    };
  },
  components: {
    MembersDropdown: httpVueLoader("/components/MembersDropdown.vue"),
  },
  mounted() {
    this.getSchoolsEnrolled();
    this.getAllSchools();
    this.getEducationLevels();
    this.getGradeLevels();
  },
  computed: {
    getModalStaus: function () {
      return this.isModalActive;
    },
  },
  methods: {
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
    msgDialog(msg, titleText = "Success") {
      this.$buefy.dialog.alert({
        title: titleText,
        message: msg,
        type: "is-success",
        hasIcon: true,
        icon: "check",
        iconPack: "fa",
        ariaRole: "alertdialog",
        ariaModal: true,
      });
    },
    getSchoolsEnrolled() {
      let vm = this;
      vm.isLoading = true;
      const params = [`household=${vm.householdId}`].join("&");
      axios
        .get(`/targeting/enrolment/schools-enrolled?${params}`)
        .then(function (response) {
          if (response.status == 200) {
            vm.schoolsEnrolled = JSON.parse(
              JSON.stringify(response.data.schools)
            );
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading school information. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    saveSchoolInfo() {
      let vm = this;
      vm.isLoading = true;

      fData = new FormData();
      fData.append("individualId", vm.memberId);
      fData.append("householdId", vm.householdId);
      fData.append("sessionId", vm.sessionId);
      fData.append("schoolId", vm.schoolId);
      fData.append("educationLevel", vm.educationLevel);
      fData.append("grade", vm.gradeLevel);
      fData.append("status", vm.status);

      const config = {
        headers: {
          "X-CSRF-TOKEN": csrf()["token"],
          "Content-Type": "multipart/form-data",
        },
      };
      axios
        .post(`/targeting/enrolment/update-school`, fData, config)
        .then(function (response) {
          if (response.status === 200) {
            vm.closeModal();
            vm.msgDialog("Updated successfully.", "", "success", "check");
            vm.getSchoolsEnrolled();
            // TODO Append the resulting entry here to the table to reflect the change
            // vm.enrollments.append(...)
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
    onHidden() {
      console.log("School modal is hidden");
    },
    getAllSchools() {
      let vm = this;
      vm.isLoading = true;
      axios
        .get(`/targeting/enrolment/schools`)
        .then(function (response) {
          if (response.status == 200) {
            vm.allSchools = response.data.schools;
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading schools. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    getEducationLevels() {
      let vm = this;
      vm.isLoading = true;
      axios
        .get(`/targeting/enrolment/education-levels`)
        .then(function (response) {
          if (response.status == 200) {
            vm.educationLevels = response.data.educationLevels;
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading education levels. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    getGradeLevels() {
      let vm = this;
      vm.isLoading = true;
      axios
        .get(`/targeting/enrolment/grade-levels`)
        .then(function (response) {
          if (response.status == 200) {
            vm.gradeLevels = response.data.gradeLevels;
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading grade levels. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    openModal(school = null, editing = false) {
      if (school) {
        this.memberId = school.individualId;
        this.schoolMemberId = school.individualId;
        this.status = school.status;
        this.schoolId = school.schoolId;
        this.educationLevel = school.educationLevel;
        this.gradeLevel = school.grade;
      } else {
        this.schoolMemberId = null;
        this.memberId = null;
        this.status = false;
        this.schoolId = null;
        this.educationLevel = null;
        this.gradeLevel = null;
      }
      this.isModalActive = true;
      this.isEditing = editing;
    },
    closeModal() {
      this.isModalActive = false;
      this.isEditing = false;
    },
  },
};
</script>
