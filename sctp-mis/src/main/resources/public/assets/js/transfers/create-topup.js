domready(() => {
    let templateEl = document.getElementById('newTopupFormTemplate');
    let appEl = document.getElementById('transfers:newTopupFormApp');

    if (!appEl || !templateEl) {
        return
    }

    var newTopupFormApp = Vue.createApp({
      template: templateEl.innerHTML,
      data() {
          return {
            funders: window.__pageData.funders || [],
            programs: window.__pageData.programs || [],
            topupForm: {
              name: '',
              programId: '',
              funderId: '',
              topupType: '',
              topupAmount: 0.0,
              isCategoricalTopUp: false,
              applyNextPeriod: false,
            },
            disabilityOptions: [
              { id: '1', description: 'Blind' },
              { id: '2', description: 'Deaf' },
              { id: '3', description: 'Speech impairment 		' },
              { id: '4', description: 'Deformed limbs' },
              { id: '5', description: 'Mentally disabled ' }
            ],
            chronicIllenssOptions: [
              { id: 1, description: 'Chronic malaria', category: 'Chronic Illness' },
              { id: 2, description: 'TB', category: 'Chronic Illness' },
              { id: 3, description: 'HIV / AIDS 		', category: 'Chronic Illness' },
              { id: 4, description: 'Asthma 			', category: 'Chronic Illness' },
              { id: 5, description: 'Arthritis 			', category: 'Chronic Illness' },
              { id: 6, description: 'Epilepsy 		', category: 'Chronic Illness' },
            ],
            orphanhoodOptions: [
              { id: 1, description: 'Single orphan' },
              { id: 2, description: 'Double orphan' },
              { id: 3, description: 'Not orphan' }
            ]
          }
        },
        computed: {
          isFixedTopup() {
            return this.topupForm.topupType === 'FIXED_AMOUNT';
          },

          isPercentageTopup() {
            return this.topupForm.topupType === 'PERCENTAGE_OF_RECIPIENT_AMOUNT';
          }
        },
        methods: {
          submit(event) {
            const url = "/transfers/topups/new";
            event.preventDefault()
            const data = {
              name: this.name,
              funderId: this.funderId,
              programId: this.programId,
              locationId: this.locationId,
              locationType: this.locationType,
              percentage: this.percentage,
              topupType: this.topupType,
              householdStatus: this.householdStatus,
              active: this.active,
              amount: this.amount,
              categorical: this.categorical,
              categoricalTargetingCriteriaId: this.categoricalTargetingCriteriaId,
              discountedFromFunds: this.discountedFromFunds,
              userId: this.userId,
              categoricalTargetingLevel: this.categoricalTargetingLevel,
              ageFrom: this.ageFrom,
              ageTo: this.ageTo,
              chronicIllnesses: this.chronicIllnesses,
              orphanhoodStatuses: this.orphanhoodStatuses,
              disabilities: this.disabilities,
            };
            axios.post(url, data)
              .then(response => {
                if (response.data) {
                    alert("Top Up saved successfully.")
                }
              })
              .catch(err => {

              })
            return false
          },

          range(start, stop, step = 1) {
            return Array.from({ length: (stop - start) / step + 1}, (_, i) => start + (i * step));
          }
        }
    })

    newTopupFormApp.mount(appEl)
});
