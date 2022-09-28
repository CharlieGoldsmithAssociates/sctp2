<template>
  <section>
    <b-message type="is-info"> Showing household summary </b-message>
    <div class="columns">
      <div class="column">
        <div class="info-list">
          <div class="info-row">
            <div class="item-label">TA</div>
            <div class="item-value">
              <span v-if="data.taName">{{ data.taName }}</span>
              <span v-else class="has-text-danger-dark">Not available</span>
            </div>
          </div>
          <div class="info-row">
            <div class="item-label">Village Cluster</div>
            <div class="item-value">
              <span v-if="data.clusterName"> {{ data.clusterName }}</span>
              <span v-else class="has-text-danger-dark">Not available</span>
            </div>
          </div>
          <div class="info-row">
            <div class="item-label">Pre-Printed Num</div>
            <div class="item-value">
              <span v-if="data.formNumber"> {{ data.formNumber }}</span>
              <span v-else class="has-text-danger-dark">Not available</span>
            </div>
          </div>
        </div>
      </div>

      <div class="column">
        <div class="info-list">
          <div class="info-row">
            <div class="item-label">Household Code</div>
            <div class="item-value">
              <span v-if="data.mlCode"> {{ data.mlCode }} </span>
              <span v-else class="has-text-danger-dark">Not available</span>
            </div>
          </div>
          <div class="info-row">
            <div class="item-label">Household Head</div>
            <div class="item-value">
              <span v-if="data.houseHoldHead">{{ data.houseHoldHead }}</span>
              <span v-else class="has-text-danger-dark">Not available</span>
            </div>
          </div>
          <div class="info-row">
            <div class="item-label">National ID Number</div>
            <div class="item-value">
              <span v-if="data.individualId">{{ data.individualId }}</span>
              <span v-else class="has-text-danger-dark">Not available</span>
            </div>
          </div>
        </div>
      </div>

      <div class="column">
        <div class="info-list">
          <div class="info-row">
            <div class="item-label">Total Household Members</div>
            <div class="item-value">
              {{ data.memberCount }}
            </div>
          </div>
          <div class="info-row">
            <div class="item-label">Total Children in Primary</div>
            <div class="item-value">
              {{ data.primaryChildren }}
            </div>
          </div>
          <div class="info-row">
            <div class="item-label">Total Children in Secondary</div>
            <div class="item-value">
              {{ data.secondaryChildren }}
            </div>
          </div>
        </div>
      </div>

      <div class="column">
        <div class="info-list">
          <div class="info-row">
            <div class="item-label">Total Enrolled Children Age 6-15</div>
            <div class="item-value">
              {{ data.childEnrollment6to15 }}
            </div>
          </div>
        </div>
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
    };
  },
  mounted() {
    this.getHouseholdSummary();
  },
  methods: {
    getHouseholdSummary() {
      let vm = this;
      vm.isLoading = true;
      const params = [
        `household=${vm.householdId}`,
        `enrollment=${vm.sessionId}`,
      ].join("&");
      axios
        .get(`/targeting/enrolment/summary?${params}`)
        .then(function (response) {
          if (response.status == 200) {
            var hasData = response.data;
            console.log("DETAILS: " + response.data);
            if (hasData) {
              if (isJsonContentType(response.headers["content-type"])) {
                vm.data = response.data;
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
            "There was an error loading households. Please try again"
          );
          console.log(error);
        })
        .then(function () {
          vm.isLoading = false;
        });
    },
    mlCode(code) {
      return code && `ML-${code}`;
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
  },
};
</script>
