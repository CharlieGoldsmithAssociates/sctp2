(function () {
  Vue.component('v-multiselect', {
    inheritAttrs: false,
    template: `
            <b-field :label="label">
                <template slot="label">
                <span class="is-flex is-justify-content-space-between is-align-items-center">
                    <span>
                        {{ label }}
                        <span v-if="required" class="has-text-danger">*</span> 
                    </span>
                    <span class="has-text-weight-light is-family-secondary is-size-6" >
                        <span @click="selectAll" :disabled="selected.size === options.length"
                         class="is-clickable has-text-primary">Select All</span> | 
                         <span @click="clear" :disabled="selected.size === 0" class="is-clickable has-text-primary">Clear</span>
                    </span>
                </span>
                </template>
                <div v-bind:class="panelClass">
                    <label v-for="option in options" class="panel-block">
                        <input type="checkbox" name="option" @change="(e) => { onChange(e.target.checked, option) }"/>
                        <span class="ml-2">{{ bindLabel(option) }}</span>
                    </label>
                </div>
                <small v-if="showSelectionCounter" class="has-text-info is-size-7 p-2">{{ selectionCounter }} / {{ options.length }} selected</small>
            </b-field>
        `,
    emits: ['input'],
    methods: {
      bindLabel(option) {
        if (this.viewBinder) {
          return this.viewBinder(option);
        } else {
          return this.optionLabelField ? option[this.optionLabelField] : option;
        }
      },
      attachCustomCss() {
        const css_text = `input[type=checkbox] {
                    -ms-transform: scale(1.5);
                    /* IE */
                    -moz-transform: scale(1.5);
                    /* FF */
                    -webkit-transform: scale(1.5);
                    /* Safari and Chrome */
                    -o-transform: scale(1.5);
                    /* Opera */
                    transform: scale(1.5);
                    padding: 10px;
                }
        
                .panel {
                    box-shadow: none !important;
                    border: 1px solid #ededed;
                    min-height: 40px;
                    max-height: 158px;
                    overflow-y: scroll;
                }`
        const css = document.createElement('style');
        css.type = 'text/css';
        css.setAttributeNode(document.createAttribute('scoped'));
        css.appendChild(document.createTextNode(css_text));
        this.$el.appendChild(css);
      },
      onChange(checked, option) {
        if (this.ignoreOptionFieldSpecifiers) {
          if (checked) {
            this.selected.add(option);
          } else {
            this.selected.delete(option);
          }
        } else {
          if (checked) {
            if (this.optionValueField) {
              this.selected.add(option[this.optionValueField]);
            } else {
              this.selected.add(option);
            }
          } else {
            if (this.optionValueField) {
              this.selected.delete(option[this.optionValueField]);
            } else {
              this.selected.delete(option);
            }
          }
        }

        if(checked){
          this.selectionCounter += 1;
        }else{
          this.selectionCounter = Math.max(this.selectionCounter - 1, 0)
        }

        this.$emit('input');
      },
      selectAll(event) {
        const checkboxes = this.$el.querySelectorAll('input[type="checkbox"]');
        for (let i = 0, n = checkboxes.length; i < n; i++) {
          checkboxes[i].checked = true;
        }

        this.options.forEach(option => {
          if (this.ignoreOptionFieldSpecifiers) {
            this.selected.add(option);
          } else {
            if (this.optionValueField) {
              this.selected.add(option[this.optionValueField]);
            } else {
              this.selected.add(option);
            }
          }
          this.selectionCounter += 1;
        });

        this.$emit('input');
      },
      clear(event) {
        const checkboxes = this.$el.querySelectorAll('input[type="checkbox"]');
        for (let i = 0, n = checkboxes.length; i < n; i++) {
          checkboxes[i].checked = false;
        }
        this.selected.clear();
        this.selectionCounter = 0;

        this.$emit('input');
      }
    },
    props: {
      viewBinder: {
        type: Function,
        default: null,
        required: false,
        validator(value) {
          return (typeof value === 'function' && value.length == 1);
        }
      },
      options: {
        type: Array,
        default: []
      },
      selected: {
        type: Set,
        default: new Set()
      },
      label: {
        type: String,
        default: null
      },
      required: {
        type: Boolean,
        default: false
      },
      optionLabelField: String | null,
      optionValueField: String | null,
      ignoreOptionFieldSpecifiers: Boolean | false,
      selectionCount: Boolean | false
    },
    data() {
      return {
        selectionCounter: 0
      }
    },
    mounted() {
      this.attachCustomCss();
    },
    computed: {
      showSelectionCounter: function() {
        return this.options.length > 0 && this.selectionCount;
      },
      panelClass: function(){
        var classes = ['panel']
        if(this.selectionCount){
          classes.push('mb-0');
        }
        return classes.join(' ');
      }
    },
    watch: {
      // clear selection when items change
      options: function (newVal, oldVal) {
        this.clear();
      }
    }
  })
})()