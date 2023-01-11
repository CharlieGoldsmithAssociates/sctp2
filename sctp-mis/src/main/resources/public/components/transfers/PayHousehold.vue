<!--
  ~ BSD 3-Clause License
  ~
  ~ Copyright (c) 2023, CGATechnologies
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
  props: {
    transferId: String
  },
  data() {
    return {
      amount: 0.0,
      comment: '',
      transfer: window.transfer || {},
      transferAgency: null,
      mainRecipient: null,
      secondaryRecipient: null,
      topupsApplicable: [],
      isLoading: false,
    }
  },

  mounted() {
  },

  methods: {
    initiatePayment() {
      const vm = this;
      let paymentRequest = {
        transferId: vm.transfer.id,
        amount: vm.amount,
        comment: vm.comment
      }
      vm.isLoading = true;
      const config = { headers: { 'X-CSRF-TOKEN': csrf()['token'], 'Content-Type': 'application/json' } }

      axios.post(`/transfers/pay/${this.transfer.id}`, paymentRequest, config)
        .then(response => {
          if (response.status === 200 && isJsonContentType(response.headers['content-type'])) {
            vm.showMessage('Processed payment')
          } else {
            vm.showErrorDialog('Failed to process request', 'warning');
          }
        })
        .catch(err => {
          vm.showErrorDialog('Failed to process request', 'warning');
        })
    }
  }
}
</script>

<template>
  <div class="transfer-entry p-3">
    <div class="card">
      <div class="card-header">
        <div class="card-header-title">Pay Transfer</div>
      </div>
      <div class="card-body p-3">
        <h3>Transfer {{transfer.id}}</h3>

        <div class="field">
          <label>Amount Collected</label>
          <div class="control">
            <input type="number" class="input" name="amount" v-model="amount">
          </div>
        </div>

        <div class="field">
          <label>Comment</label>
          <div class="control">
            <textarea name="comment" cols="30" rows="2" class="input" v-model="comment"></textarea>
          </div>
        </div>
        <button class="button is-primary">Pay Household</button>
      </div>
    </div>
  </div>
</template>