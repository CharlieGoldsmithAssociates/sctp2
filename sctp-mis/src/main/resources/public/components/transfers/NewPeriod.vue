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
<script>
module.exports = {
    data() {
        return {
            newPeriodForm: {},
        }
    }
}
</script>
<template>
    <div id="transfers:CreateNewPeriodApp" class="content-container">
        <div class="card">
            <div class="card-header">
                <div class="column">
                    <h2 class="title">Open a New Transfer Period</h2>
                </div>
            </div>
            <form method="post" action="/transfers/periods/open-new" enctype="application/x-www-form-urlencoded">
                {{ csrf(_csrf) }}
                <div class="card-content">
                    {{ showMessages(successMessage, infoMessage, dangerMessage, warningMessage) }}
                    <div class="columns-stuff">
                        <div class="field">
                            <div class="is-normal">
                                <label class="label is-required">Program</label>
                            </div>
                            <div class="field-body">
                                <div class="field">
                                    <div class="control">
                                        <div class="select is-fullwidth">
                                            {{ selectField('programId', programs, form.programId, true) }}
                                        </div>
                                    </div>
                                    {{ printFieldErrors(getFieldErrors('form', 'programId')) }}
                                </div>
                            </div>
                        </div>
                        <div class="field">
                            <div class="is-normal">
                                <label class="label is-required">District</label>
                            </div>
                            <div class="field-body">
                                <div class="field">
                                    <div class="control">
                                        <div class="select is-fullwidth">
                                            {{ selectField('districtId', districts, form.districtId, true) }}
                                        </div>
                                    </div>
                                    {{ printFieldErrors(getFieldErrors('form', 'districtId')) }}
                                </div>
                            </div>
                        </div>
                        <div class="columns">
                            <div class="column is-one-quarter">
                                <div class="field">
                                    <label class="label is-required">Start Date</label>
                                    <div class="control">
                                        {{ dateField('startDate', form.startDate, true) }}
                                    </div>
                                    {{ printFieldErrors(getFieldErrors('form', 'startDate')) }}
                                </div>
                            </div>
                            <div class="column is-one-quarter">
                                <div class="field">
                                    <label class="label">End Date</label>
                                    <div class="control">
                                        {{ dateField('endDate', form.endDate, false) }}
                                    </div>
                                    {{ printFieldErrors(getFieldErrors('form', 'endDate')) }}
                                </div>
                            </div>
                        </div>
                        <div class="columns">
                            <div class="buttons is-right">
                                <button type="submit" class="button is-success">Open Transfer Period &gt;&gt;</button>
                                <a href="/transfers/summary">Cancel</a>
                            </div>
                        </div>


                        <div class="period-parameters">
                            <div class="new-transfer-period-params has-text-centered">
                                <div class="month-preview">
                                    <table class="table is-full-width is-striped">
                                        <thead>
                                        <tr>
                                            <th></th>
                                            <th>Jan</th>
                                            <th>Feb</th>
                                            <th>Mar</th>
                                            <th>Apr</th>
                                            <th>May</th>
                                            <th>Jun</th>
                                            <th>Jul</th>
                                            <th>Aug</th>
                                            <th>Sep</th>
                                            <th>Oct</th>
                                            <th>Nov</th>
                                            <th>Dec</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td>2022</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                        </tr>
                                        <tr>
                                            <td>2023</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                            <td>&times;</td>
                                        </tr>
                                        </tbody>
                                        <tr class=""></tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</template>