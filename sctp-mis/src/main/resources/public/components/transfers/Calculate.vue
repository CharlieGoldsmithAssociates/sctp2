<!--
  ~ BSD 3-Clause License
  ~
  ~ Copyright (c) 2022, CGATechnologies
  ~ All rights reserved.
  ~
  ~ Redistribution and use in source and binary forms, with or without
  ~ modification, are permitted provided that the following conditions are met:
  ~
  ~ 1. Redistributions of source code must retain the above copyright notice, this
  ~    list of conditions and the following disclaimer.
  ~
  ~ 2. Redistributions in binary form must reproduce the above copyright notice,
  ~    this list of conditions and the following disclaimer in the documentation
  ~    and/or other materials provided with the distribution.
  ~
  ~ 3. Neither the name of the copyright holder nor the names of its
  ~    contributors may be used to endorse or promote products derived from
  ~    this software without specific prior written permission.
  ~
  ~ THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  ~ AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  ~ IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  ~ DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  ~ FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  ~ DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  ~ SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  ~ CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  ~ OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  ~ OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  -->
<template>

    {# TODO(zikani03) Move the stylesheets to a file #}
    <style type="text/css" rel="stylesheet">
    .has-80-smaller-font {
        font-size: 80%;
    }
    table.summary-table thead th,
    table.summary-table tbody th,
    table.summary-table tbody td {
        border-top-color: rgba(31, 85, 156, 0.15);
        border-bottom-color: rgba(31, 85, 156, 0.15);
    }
</style>
    <div id="transfers:CalculateTransfersApp">
        <div class="card">
            <div class="card-header">
                <div class="column">
                    <h2 class="title">Perform Transfer Calculations</h2>
                </div>
            </div>
            <div class="card-content">
                <div class="columns">
                    <div class="column is-one-third">
                        <div class="transfer-agency">
                            <label class="label">Program</label>
                            <span  v-text="enrollmentSession.programName"></span>
                        </div>
                    </div>
                    {#                    <div class="column is-one-third">#}
                    {#                        <div class="transfer-agency">#}
                        {#                            <label class="label">District</label>#}
                        {#                            <span>TODO</span>#}
                        {#                        </div>#}
                    {#                    </div>#}
                    {#                    <div class="column">#}
                    {#                        <div class="district location">#}
                        {#                            <label class="label">Period Duration</label>#}
                        {#                            <span>From <strong v-text="enrollmentSession.programName">{{programInfo.startDate}}</strong> to <strong>{{programInfo.endDate}}</strong></span> (24  months)#}
                        {#                        </div>#}
                    {#                    </div>#}
                </div>
            </div>
            <div class="card-content">
                <div class="columns">
                    <div class="column is-one-third">
                        <div class="district location">
                            <div class="period-header">
                                <label class="label">Transfer Period Months</label>
                            </div>
                            <div class="field">
                                <label for="numberOfMonths" class="label">Duration (in Months)</label>
                                <div class="control">
                                    <input type="number" v-model="transferPeriod.numberOfMonths" placeholder="1" class="input is-danger" />
                                </div>
                            </div>
                            <!-- <div class="transfer-period-definition columns">
                                <div class="field column">
                                    <label for="start" class="label">Start</label>
                                    <div class="control"><input type="text" placeholder="Month Year, e.g. Jan-22" v-model="transferPeriod.startDate" class="input"></div>
                                </div>
                                <div class="field column">
                                    <label for="end" class="label">End</label>
                                    <div class="control"><input type="text" placeholder="Month Year, e.g. Dec-22" v-model="transferPeriod.endDate" class="input"></div>
                                </div>
                            </div>
                            <div class="is-clearfix">
                                <button class="button is-primary is-small">Update Period</button>
                            </div> -->
                            <p>
                                <a href="" class="is-size-7">Show Visual Preview of Period</a>
                            </p>
                        </div>
                    </div>
                </div>
                <hr/>
                <div class="container">
                    <div class="action-buttons">
                        <div class="buttons are-default is-right">
                            <button @click="handlePerformPreCalculation" class="button is-warning">Run Pre-Calculations</button>
                            <button class="button is-primary" @click="savePrecalculatedAmounts">Save Pre-Calculations</button>
                            <button class="button is-danger" disabled>Run Final Calculations</button>
                        </div>
                    </div>
                    <div class="columns">
                        <div class="column is-half">
                            <div class="field">
                                <label for="" class="label">Search</label>
                                <p class="control is-expanded">
                                    <input class="input" type="text" placeholder="Search for Household">
                                </p>
                                <div class="control is-right">
                                    <button class="button is-default is-small is-right">Search</button>
                                </div>
                            </div>
                        </div>
                        <div class="column is-half">
                            <div class="field">
                                <div class="control">
                                    <label class="label">Filter Data By</label>
                                    <div class="control">
                                        <div class="select">
                                            <select class="input select" name="" id="">
                                                <option value="">Select an Option</option>
                                                <option value="">First Time Transfer Households</option>
                                                <option value="">Households with Transfers Performed</option>
                                                <option value="">Households without Transfers</option>
                                                <option value="">Households with Changes on Transfers</option>
                                                <option>----</option>
                                                <option value="">Households with Any Arrears</option>
                                                <option value="">Households with Uncollected Arrears</option>
                                                <option value="">Households with Other Arrears</option>
                                                <option>----</option>
                                                <option value="">Houses that haven't been Reconciled</option>
                                            </select>
                                        </div>
                                        <button class="button is-success">Filter</button>
                                    </div>
                                </div>
                            </div>
                            <div class="form">
                                <label class="control label" for="">
                                    <input class="control" type="radio" name="showHouseholdsOrAmounts" value="all"> Show Households and Amounts
                                    <input class="control" type="radio" name="showHouseholdsOrAmounts" value="householdsOnly"> Show Only Households
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="columns">
                        <div class="column">
                            <div class="action-buttons">
                                <div class="buttons has-addons are-small is-left">
                                    <button class="button is-default">Export Households to CSV</button>
                                    <button class="button is-default">Export Households to Excel</button>
                                    <button class="button is-default">Export Suspended Households Report</button>

                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="columns has-text-centered has-80-smaller-font">
                        <table class="table is-bordered is-narrow is-fullwidth">
                            <thead>
                            <tr>
                                <th>TA</th>
                                <th>Village Cluster</th>
                                <th>Zone</th>
                                <th>HH Code</th>
                                <th>Receiver Name &amp; Code</th>
                                <th># of Members</th>
                                <th>Primary</th>
                                <th>Primary Incentive</th>
                                <th>Secondary</th>
                                <th>Secondary Incentive</th>
                                <th>Monthly Amount</th>
                                <th>No. Of Months</th>
                                <th>Total Monthly Amount</th>
                                <th rowspan="0.5">Arrears</th>
                                <th>Total Amount</th>
                                <th>Is First Transfer</th>
                                {#                            <th>To Be Delivered</th>#}
                                {#                            <th>Reconciliation</th>#}
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr v-for="row in householdRows">
                                <td v-text="row.taName"></td>
                                <td v-text="row.clusterName"></td>
                                <td v-text="row.zoneName"></td>
                                <td v-text="row.mlCode"></td>
                                <td v-text="row.receiverName"></td>
                                <td v-text="row.memberCount"></td>
                                <td v-text="row.primaryChildren"></td>
                                <td v-text="numberFormatter.format(row.primaryIncentive)"></td>
                                <td v-text="row.secondaryChildren"></td>
                                <td v-text="numberFormatter.format(row.secondaryIncentive)"></td>
                                <td v-text="numberFormatter.format(row.monthlyAmount)"></td>
                                <td v-text="row.numberOfMonths"></td>
                                <td v-text="numberFormatter.format(row.totalMonthlyAmount)"></td>
                                <td v-text="numberFormatter.format(row.totalArrears)"></td>
                                <td v-text="numberFormatter.format(row.totalAmount)"></td>
                                <td v-text="row.isFirstTransfer"></td>
                                {#                            <td v-text="row.isDelivered"></td>#}
                                {#                            <td v-text="row.isReconciled"></td>#}
                                <td>
                                    <div class="dropdown is-hoverable">
                                        <div class="dropdown-trigger">
                                            <button class="button button is-info is-inverted"
                                                    aria-haspopup="true" aria-controls="dropdown-menu2">
                                                <span>Options</span>
                                                <span class="icon is-small">
                                                <i class="fas fa-angle-down" aria-hidden="true"></i>
                                            </span>
                                            </button>
                                        </div>
                                        <div class="dropdown-menu" id="dropdown-menu2" role="menu">
                                            <div class="dropdown-content">
                                                <a class="dropdown-item">Preview</a>
                                                <a class="dropdown-item">Add Top Up</a>
                                                <a class="dropdown-item">Suspend Household</a>
                                                <a class="dropdown-item">Exit from Transfers</a>
                                            </div>
                                        </div>
                                    </div>

                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <div class="summary-section">
            <div class="card is-narrow">
                <div class="card-content has-text-centered is-size-7 is-uppercase" style="background: rgba(51, 139, 253, 0.45)">
                    <table class="summary-table table" style="background: none;">
                        <thead>
                        <tr>
                            <th colspan="7">Transfer SUMMARY</th>
                            <th>Informative Amounts</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <th></th>
                            <th>No. Households</th>
                            <th>Period Amount</th>
                            <th>Uncollected Arrears</th>
                            <th>Previous Other Arrears</th>
                            <th>HH Outgoing Arrears</th>
                            <th>Funds to be Requested</th>
                            <th>HH Incoming Arrears</th>
                        </tr>
                        <tr>
                            <td>To Transfer</td>
                            <td v-text="numberFormatter.format(transferSessionSummary.totalHouseholds)"></td>
                            <td v-text="numberFormatter.format(transferSessionSummary.totalAmountToBeDisbursed)"></td>
                            <td v-text="numberFormatter.format(transferSessionSummary.totalUncollectedArrears)"></td>
                            <td v-text="numberFormatter.format(transferSessionSummary.totalOtherArrears)"></td>
                            <td v-text="numberFormatter.format(transferSessionSummary.totalOutgoingArrears)"></td>
                            <td v-text="numberFormatter.format(transferSessionSummary.totalFundsToRequest)"></td>
                            <td v-text="numberFormatter.format(transferSessionSummary.totalIncomingArrears)"></td>
                            <td>0</td>
                        </tr>
                        </tbody>
                    </table>
                    <em><small>This Summary is created / updated after a Calculation has been run.</small></em>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
module.exports = {
        data: () => {
            return {
                transferSession: data.transferSession || {},
                // Each session may be attached to one or many districts...
                sessionDistricts: [],
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

            savePrecalculatedAmounts() {
                axios.post(["/transfers/sessions/", this.transferSession.id, "/pre-calculation"].join(""))
                    .then((response) => response.data)
                    .then((data) => alert('Pre-Calculations saved!'))
                    .catch(err => console.log(err))
            }
        }
}
</script>