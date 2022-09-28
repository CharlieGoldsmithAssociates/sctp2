<template>
  <section>
    <div class="content-tab">
      <b-message type="is-info">
        Add or edit information about school going children under this
        household.
      </b-message>

      <section>
        <b-table
          :data="isEmpty ? [] : children"
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
            label="Name of child"
            v-slot="props"
          >
            {{ props.row.first_name }}
          </b-table-column>

          <b-table-column
            field="HH Member Code"
            label="HH Member Code"
            v-slot="props"
          >
            {{ props.row.last_name }}
          </b-table-column>

          <b-table-column
            field="currentSchool"
            label="Current School"
            v-slot="props"
          >
            <span class="tag is-success"> </span>
          </b-table-column>

          <b-table-column label="Education Level" v-slot="props">
            <span> </span>
          </b-table-column>

          <b-table-column label="Grade" v-slot="props">
            <span> </span>
          </b-table-column>

          <b-table-column label="Select School" v-slot="props">
            <span> </span>
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
              @click="openCloseSchoolModal(!isSchoolModalActive)"
            />
          </div>
        </div>
        <div class="column">
          <div class="buttons is-right">
            <b-button type="is-info" @click="getSchoolChildren()">
              Reload
            </b-button>
            <b-button type="is-primary" @click="saveSchoolChildren">
              Save
            </b-button>
          </div>
        </div>
      </div>
    </div>

    <School-Modal :is-active="isSchoolModalActive" :household-id="householdId" :session-id="sessionId" />

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
      required: true
    }
  },
  data() {
    return {
      children: [],
      isEmpty: false,
      isBordered: false,
      isStriped: true,
      isNarrowed: true,
      isHoverable: true,
      isFocusable: true,
      isLoading: false,
      hasMobileCards: true,
      isSchoolModalActive: false,
    };
  },
  components: {
    'SchoolModal': httpVueLoader( "/components/SchoolModal.vue"),
  },
  mounted() {
    this.getSchoolChildren();
  },
  computed: {
    getModalStaus: function(){
      return this.isSchoolModalActive;
    }
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
    getSchoolChildren() {
      let vm = this;
      vm.isLoading = true;
      const params = [`household=${vm.householdId}`].join("&");
      axios
        .get(`/targeting/enrolment/school-children?${params}`)
        .then(function (response) {
          if (response.status == 200) {
            var hasData =
              response.data.children && response.data.children.length > 0;

            if (hasData) {
              if (isJsonContentType(response.headers["content-type"])) {
                vm.children = response.data.children;
                vm.isEmpty = false;
              } else {
                throw "invalid type";
              }
            } else {
              vm.isEmpty = true;
            }
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
    saveSchoolChildren() {},
    openCloseSchoolModal(status){
      this.isSchoolModalActive = status
    }
  },
};
</script>
