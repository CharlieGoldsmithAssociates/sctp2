function domready(fn) {
  if (document.readyState != 'loading'){
    fn();
  } else {
    document.addEventListener('DOMContentLoaded', fn);
  }
}

domready(() => {
    let app = Vue.createApp({
        data: () => {
            return {
                transferSession: data.transferSession || {},
                transferSessionSummary: {
                    totalHouseholds: 0,
                    totalAmountToBeDisbursed: 0,
                    totalUncollectedArrears: 0,
                    totalOtherArrears: 0,
                    totalOutgoingArrears: 0,
                    totalFundsToRequest: 0,
                    totalIncomingArrears: 0,
                },
                programInfo: data.programInfo || {},
                enrollmentSession: data.enrollmentSession || {},
                householdRows: data.householdRows || [],
                // this structure holds the arrears as key-value db with the household-ml-code as the key
                // the values are the arrears amounts for the household as at the given date.
                householdArrears: {
                    'household-ml-code': {
                        totalArrears: 0,
                    }
                },
                transferAgencyOptions: data.transferAgencyOptions,
                transferAgency: data.transferAgency | {},
                transferPeriod: {
                    numberOfMonths: 1,
                    startDate: '',
                    endDate: '',
                },
                transferPeriods: [],
                householdParams: data.householdParams || [],
                educationParams: data.educationParams || {},
                numberFormatter: Intl.NumberFormat(),
            }
        },

        methods: {
            handlePerformPreCalculation(event) {
                event.preventDefault();
                this.performPrecalculation(this.transferAgency, this.transferPeriods)
                return false;
            },


            /**
             * Gets basic amount by the household parameters based on the size of the household
             */
            getAmountByHouseholdSize(householdSize) {
                // TODO(zikani03): Optimize this implementation by adjusting the structure of the params
                for(var param of this.householdParams) {
                    if (param.condition == 'GREATER_THAN' &&  householdSize > param.numberOfMembers) {
                        return param.amount;
                    } else if (param.condition == 'GREATER_THAN_EQUALS' &&  householdSize >= param.numberOfMembers) {
                        return param.amount;
                    } else if (param.condition == 'EQUALS' && householdSize == param.numberOfMembers) {
                        return param.amount;
                    }
                }

                // TODO: What happens if we get here?
                return -1;
            },

            performPrecalculation(agency, periods) {
                const that = this;
                let totalDisbursement = 0;
                this.householdRows.forEach(household => {
                    household.primaryIncentive = household.primaryChildren * that.educationParams['primary'].amount;

                    household.secondaryIncentive = household.secondaryChildren * that.educationParams['secondary'].amount;

                    household.monthlyAmount = that.getAmountByHouseholdSize(household.memberCount)

                    household.numberOfMonths = that.transferPeriod.numberOfMonths;

                    household.totalMonthlyAmount = that.transferPeriod.numberOfMonths * household.monthlyAmount;

                    household.totalAmount = household.totalMonthlyAmount + household.primaryIncentive + household.secondaryIncentive;

                    totalDisbursement += household.totalAmount;
                })

                this.transferSessionSummary.totalHouseholds = this.householdRows.length;
                this.transferSessionSummary.totalAmountToBeDisbursed = totalDisbursement;
                this.transferSessionSummary.totalFundsToRequest = totalDisbursement;// TODO: programInfo.currentFundsBalance - totalDisbursement;
                // TODO: perform arrears calculations
                // calculateArrearsAmounts()
                // updateTotalAmounts()
            },
        }
    })

    app.mount(document.getElementById('transfers:CalculateTransfersApp'))
})