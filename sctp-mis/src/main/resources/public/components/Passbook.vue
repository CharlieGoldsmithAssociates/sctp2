<template>
  <section>
    <b-message type="is-info"> View/print household passbook. </b-message>

    <section></section>
  </section>
</template>

<script>
module.exports = {
  props: {
    householdId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      data: [],
    };
  },
  mounted() {
  },
  computed: {},
  methods: {},
};
</script>
