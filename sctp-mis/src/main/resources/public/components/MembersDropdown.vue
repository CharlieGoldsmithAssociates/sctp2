<template>
  <b-select
    placeholder="Select Household Member"
    required
    expanded
    v-model="memberId"
    @input="changeMemberId"
    name="memberId"
  >
    <option
      v-for="individual in candidates"
      :value="individual.id"
      :selected="editingMemberId == individual.id"
    >
      {{ individual.name }} ({{ individual.gender }}, {{ individual.age }} yrs)
      MemberID {{ editingMemberId == individual.id }}
    </option>
  </b-select>
</template>

<!-- :disabled="!isEditing" -->
<script>
module.exports = {
  props: {
    householdId: {
      type: Number,
      required: true,
    },
    isEditing: {
      type: Boolean,
      required: false,
    },
    editingMemberId: {
      type: Number,
      required: false,
    },
  },
  data() {
    return {
      candidates: [],
      memberId: null,
    };
  },
  mounted() {
    this.getRecipientCandidates();
  },
  methods: {
    getRecipientCandidates() {
      let vm = this;
      vm.isLoading = true;
      const params = [`household=${vm.householdId}`].join("&");
      axios
        .get(`/targeting/enrolment/recipient-candidates?${params}`)
        .then(function (response) {
          if (response.status == 200) {
            vm.candidates = response.data.candidates;
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog(
            "There was an error loading recipient candidates. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
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
    changeMemberId() {
      this.$emit("input", this.memberId);
    },
  },
};
</script>
