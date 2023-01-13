<script>
module.exports = {
  mounted() {
    this.getActiveDistricts();
  },
  props: {
    loading: {
      type: Boolean,
      required: false,
      default: false
    },
    optionLabelField: {
      type: String,
      default: 'text'
    },
    optionValueField: {
      type: String,
      default: 'id'
    },
    selectionCount: {
      type: Boolean,
      default: true
    },
    listAdapter: {
      type: Object,
      default: null,
      validator(value) {
        objectHasProperty = function (obj, prop, type) {
          return (prop in obj) && [typeof obj[prop], Object.prototype.toString.call(obj[prop])].includes(type);
        };

        return objectHasProperty(value, 'loadDistricts', 'function')
          && objectHasProperty(value, 'loadTraditionalAuthorities', 'function')
          && objectHasProperty(value, 'loadVillageClusters', 'function')
          && objectHasProperty(value, 'bindItemView', 'function');
      }
    }
  },

  data() {
    return {
      isLoading: false,
      selectedDistrict: null,
      districts: [],
      villageClusters: [],
      traditionalAuthorities: [],
      selectedVillageClusters: new Set(),
      selectedTraditionalAuthorities: new Set(),
      defaultLocationAdapter: {
        loadDistricts() {
          const vm = this;
          return new Promise((resolve, reject) => {
            vm.isLoading = true

            axios.get('/locations/districts/active')
              .then(function (response) {
                if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
                  resolve(response.data);
                } else {
                  reject(new Error('invalid content type'))
                }
              })
              .catch(function (error) {
                reject(error)
              })
              .then(function () {
                vm.isLoading = false
              });
          })
        },
        loadTraditionalAuthorities(districtCode) {
          const vm = this;

          return new Promise((resolve, reject) => {
            vm.isLoading = true;

            axios.get(`/locations/get-child-locations?id=${districtCode}`)
              .then(function (response) {
                if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
                  resolve(response.data);
                } else {
                  reject(new Error('invalid content type'));
                }
              })
              .catch((error) => reject(error))
              .then(() => vm.isLoading = false);
          })
        },
        loadVillageClusters(parentTaCodes) {
          const vm = this;

          return new Promise((resolve, reject) => {
            if (parentTaCodes.size === 0) {
              resolve([]);
              return;
            }

            vm.isLoading = true;

            axios.get(`/locations/get-child-locations/multiple?ids=${[...parentTaCodes]}`)
              .then(function (response) {
                if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
                  resolve(response.data);
                } else {
                  reject(new Error('invalid content type'));
                }
              })
              .catch((error) => reject(error))
              .then(() => vm.isLoading = false);
          })
        },
        bindItemView: function (location) {
          return location[vm.optionLabelField];
        }
      }
    }
  },
  emits: ['clustersupdated'],
  computed: {
    isLoaderActive() {
      return this.isLoading || this.loading;
    }
  },

  methods: {
    snackbar(msg, msgType = 'info') {
      this.$buefy.toast.open({
        duration: 5000,
        message: msg,
        position: 'is-bottom',
        type: 'is-' + msgType
      })
    },

    dataSource() {
      return this.listAdapter ? this.listAdapter : this.defaultLocationAdapter;
    },

    viewBinder(item) {
      return this.dataSource().bindItemView(item)
    },

    getActiveDistricts() {
      let vm = this;
      vm.dataSource()
        .loadDistricts()
        .then(data => vm.districts = data)
        .catch(() => vm.snackbar('Error getting districts', 'danger'));
    },

    getTraditionalAuthorities() {
      let vm = this;
      vm.dataSource()
        .loadTraditionalAuthorities(vm.selectedDistrict.code)
        .then(data => vm.traditionalAuthorities = data)
        .catch(() => vm.snackbar('Error loading traditional authorities', 'danger'));
    },

    getVillageClusters() {
      let vm = this;
      vm.dataSource()
        .loadVillageClusters(vm.selectedTraditionalAuthorities)
        .then(data => vm.villageClusters = data)
        .catch(() => vm.snackbar('Error loading village clusters', 'danger'));
    },

    clusterSelectionUpdated() {
      this.$emit('clustersupdated', [...this.selectedVillageClusters])
    }
  }
}
</script>
<template>
  <section>
    <b-field>
      <template slot="label">
        <span>District <span class="has-text-danger">*</span> </span>
      </template>
      <b-select placeholder="Select a district" v-model="selectedDistrict" @input="getTraditionalAuthorities" expanded
        required>
        <option v-for="option in districts" :value="option" :key="option[this.optionValueField]">
          {{ viewBinder(option) }}
        </option>
      </b-select>
    </b-field>

    <div class="columns">

      <div class="column">
        <v-multiselect label="T/A" :required="true" :options="traditionalAuthorities" :selection-count="selectionCount"
          :option-label-field="optionLabelField" :option-value-field="optionValueField" :view-binder="viewBinder"
          :selected="selectedTraditionalAuthorities" @input="getVillageClusters">
        </v-multiselect>
      </div>

      <div class="column">
        <v-multiselect label="Village Clusters" :required="true" :options="villageClusters"
          :selection-count="selectionCount" :option-label-field="optionLabelField"
          :option-value-field="optionValueField" :view-binder="viewBinder" :selected="selectedVillageClusters"
          @input="clusterSelectionUpdated"></v-multiselect>
      </div>

    </div>
    <b-loading :is-full-page="false" :active="isLoaderActive"></b-loading>
  </section>
</template>
