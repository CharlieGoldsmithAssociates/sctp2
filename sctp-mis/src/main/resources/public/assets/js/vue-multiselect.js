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
                        <span @click="selectAll" disabled="selected.size() === options.length"
                         class="is-clickable has-text-primary">Select All</span> | 
                         <span @click="clear" disabled="selected.size() === 0" class="is-clickable has-text-primary">Clear</span>
                    </span>
                </span>
                </template>
                <div class="panel">
                    <label v-for="option in options" class="panel-block">
                        <input type="checkbox" name="option" @change="(e) => { onChange(e.target.checked, option) }"/>
                        <span class="ml-2">{{ optionLabelField ? option[optionLabelField] : option }}</span>
                    </label>
                </div>
            </b-field>
        `,
        emits: ['input'],
        methods: {
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

                this.$emit('input');
            },
            selectAll(event) {
                const checkboxes = this.$el.querySelectorAll('input[type="checkbox"]');
                for(let i = 0 , n = checkboxes.length; i < n; i++) {
                    checkboxes[i].checked = true;
                }

                this.options.forEach(option => {
                    if (this.optionValueField) {
                        this.selected.add(option[this.optionValueField]);
                    } else {
                        this.selected.add(option);
                    }
                });

                this.$emit('input');
            },
            clear(event) {
                const checkboxes = this.$el.querySelectorAll('input[type="checkbox"]');
                for(let i = 0 , n = checkboxes.length; i < n; i++) {
                    checkboxes[i].checked = false;
                }
                this.selected.clear();

                this.$emit('input');
            }
        },
        props: {
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
            optionValueField: String | null
        },
        data() {
            return {

            }
        },
        mounted() {
            this.attachCustomCss();
        },
    })
})()