<template>
  <section>
    <div class="content mb-0">

      <b-field grouped>
        <b-field label="Program" custom-class="is-required" class="flex-grow">
          <b-select placeholder="Select Program" v-model="newSessionData.programId" expanded required>
            <option v-for="program in programs" :value="program.id" :key="program.id">
              {{ program.text }}
            </option>
          </b-select>
        </b-field>

        <b-field label="Targeting Criteria" custom-class="is-required" class="flex-grow">
          <b-select placeholder="Select a criteria set" v-model="newSessionData.criterionId" expanded required>
            <option v-for="criterion in criteria" :value="criterion.id" :key="criterion.id">
              {{ criterion.text }}
            </option>
          </b-select>
        </b-field>
      </b-field>

      <multi-location-selector :list-adapter="listAdapter" @clustersupdated="(c) => newSessionData.clusterCodes = c"
        :loading="isLoading" option-label-field="name" option-value-field="code" />
    </div>

    <div class="level">
      <div class="level-left">
        <div class="level-item">
          <b-button @click="countEligibleHouseholds" type="is-info" :disabled="disableCountButton" :loading="isLoading">
            Count Matching Households</b-button>
        </div>
        <p class="level-item"><strong>{{ matchingHouseholdCount.toLocaleString() }}</strong></p>
      </div>
      <div class="level-right">
        <p class="level-item">
          <b-button @click="createEligibilitySession" type="is-primary" :disabled="disableRunButton"
            :loading="isLoading">Run Pre-Eligibility Verification</b-button>
        </p>
      </div>
    </div>

  </section>
</template>

<style scoped>
.flex-grow {
  flex-grow: inherit;
}
</style>

<script>

module.exports = {
  props: {

  },
  data() {
    let vm = this;
    return {
      isLoading: false,
      criteria: [],
      programs: [],
      matchingHouseholdCount: 0,
      newSessionData: {
        programId: 0,
        districtCode: 0,
        taCodes: [],
        clusterCodes: [],
        criterionId: 0
      },
      listAdapter: {
        loadDistricts() { return vm.districtLoader() },
        loadTraditionalAuthorities(districtCodes) { return vm.taLoader(districtCodes) },
        loadVillageClusters(taCodes) { return vm.clusterLoader(taCodes) },
        bindItemView: function (location) {
          return `${location.name} (${location.household_count.toLocaleString()})`;
        }
      }
    }
  },
  components: {
    'MultiLocationSelector': httpVueLoader('/components/locations/MultiLocationSelector.vue')
  },
  mounted() {
    this.loadNewSessionData();
  },
  computed: {
    hasValidCountData() {
      return this.newSessionData.districtCode > 0
        && this.newSessionData.criterionId > 0
        && this.newSessionData.taCodes.length > 0
        && this.newSessionData.clusterCodes.length > 0;
    },
    hasValidSessionData() {
      return this.newSessionData.programId > 0 && this.hasValidCountData;
    },
    disableCountButton() {
      return !this.hasValidCountData;
    },
    disableRunButton() {
      return !this.hasValidSessionData
    }
  },
  methods: {

    districtLoader() {
      return this.getHouseholdLocations('SUBNATIONAL1', []);
    },

    taLoader(districtCode) {
      this.newSessionData.districtCode = districtCode;
      return this.getHouseholdLocations('SUBNATIONAL2', [districtCode]);
    },

    clusterLoader(taCodes) {
      this.newSessionData.taCodes = [...taCodes];
      return this.getHouseholdLocations('SUBNATIONAL3', taCodes);
    },

    getHouseholdLocations(type, parentCodes) {
      let vm = this;
      return new Promise((resolve, reject) => {
        if (type != 'SUBNATIONAL1' && parentCodes.size == 0) {
          resolve([])
          return
        }
        const config = {
          headers: { 'X-CSRF-TOKEN': csrf()['token'], "Content-Type": "application/json" }
        };
        vm.isLoading = true;
        axios.post(`/locations/get-bulk-household-locations/${type}`, { 'parentCodes': [...parentCodes] }, config)
          .then(function (response) {
            if (response.status == 200) {
              if (isJsonContentType(response.headers['content-type'])) {
                resolve(response.data);
              } else {
                reject(new Error(`Server returned invalid type: ${response.headers['content-type']}`));
              }
            } else {
              reject(new Error(`Server returned: ${response.status}`))
            }
          })
          .catch((error) => reject(error))
          .then(() => vm.isLoading = false);
      });
    },

    loadNewSessionData() {
      let vm = this;
      vm.isLoading = true;
      axios.get('/verification/get-new-session-data')
        .then(function (response) {
          if (response.status == 200) {
            if (isJsonContentType(response.headers['content-type'])) {
              vm.criteria = response.data.criteria;
              vm.programs = response.data.programs;
            } else {
              throw 'invalid type';
            }
          } else {
            throw `Server returned: ${response.status}`;
          }
        })
        .catch(function (error) {
          vm.errorDialog('There was an error connecting to the server. Please try again');
        })
        .then(function () {
          vm.isLoading = false;
        });
    },

    countEligibleHouseholds() {
      let vm = this;
      const config = {
        headers: { 'X-CSRF-TOKEN': csrf()['token'], "Content-Type": "application/json" }
      };
      vm.isLoading = true;
      axios.post('/verification/count-matching-households', vm.newSessionData, config)
        .then(function (response) {
          if (response.status == 200) {
            vm.matchingHouseholdCount = parseInt(response.data);
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
    createEligibilitySession() {
      let vm = this;
      vm.confirm(
        'Run Pre-Eligibility Verification?',
        function () {
          const errorMessage = 'There was an error processing the request. Please try again';
          const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'], "Content-Type": "application/json" } };

          vm.isLoading = true;

          axios.post('/verification', vm.newSessionData, config)
            .then(function (response) {
              if (response.status == 200) {
                vm.sessionId = parseInt(response.data);
                vm.snackbar('Operation completed successfully. Redirecting...')
                setTimeout(() => document.location.href = `/verification/review?id=${vm.sessionId}`, 2000);
              } else if (response.status == 406) {
                vm.errorDialog('Selected targeting criteria cannot be used because it does not contain filters.')
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
        () => vm.snackbar('Operation cancelled', 'info'),
        'Confirm action',
        'warning'
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