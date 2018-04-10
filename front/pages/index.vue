<template>
  <section class="columns">
    <div class="column is-4 is-info is-offset-5">
    <form v-if="flag" v-on:submit.prevent="mandar2()">
      <div class="control">
      <label class="checkbox">
        <input type="checkbox" v-model="bits.led1">
        Pin 1
      </label>
      </div>
      <div class="control">
      <label class="checkbox">
        <input type="checkbox" v-model="bits.led2">
        Pin 2
      </label>
      </div>
      <div class="control">
      <label class="checkbox">
        <input type="checkbox" v-model="bits.led3">
        Pin 3
      </label>
      </div>
      <div class="control">
      <label class="checkbox">
        <input type="checkbox" v-model="bits.led4">
        Pin 4
      </label>
      </div>
      <div class="control">
      <label class="checkbox">
        <input type="checkbox" v-model="bits.led5">
        Pin 5
      </label>
      </div>
      <div class="control">
      <label class="checkbox">
        <input type="checkbox" v-model="bits.led6">
        Pin 6
      </label>
      </div>
      <div class="control">
        <label class="checkbox">
          <input type="checkbox" v-model="bits.led7">
          Pin 7
        </label>
      </div>
      <div class="control">
      <label class="checkbox">
        <input type="checkbox" v-model="bits.led8">
        Pin 8
      </label>
      </div>
      <br/>
      <div class="field is-grouped">
        <div class="control">
          <button class="button is-link">Submit</button>
        </div>
      </div>
    </form>
    </div>
  </section>
</template>

<script>
export default {
data() {
  return {
    flag: false,
    bits: {
      led1: false,
      led2: false,
      led3: false,
      led4: false,
      led5: false,
      led6: false,
      led7: false,
      led8: false
    }
  }
},
  mounted() {
  this.mandar()
  },
methods: {
  async mandar2() {
      await this.$axios.post("http://localhost:8080/prender", this.bits).then(response => {
        console.log(JSON.stringify(response));
      }).catch(function(error) {
        console.log(error);
        this.$toast.open({
          duration: 5000,
          message: error.response.data.error,
          position: "is-bottom",
          type: "is-danger"
        });
      });
  },
  async mandar() {
  try{
    console.log("Aqui")
    await this.$axios.get("http://localhost:8080/checar")
    flag = true
  }catch (e)
  {
    this.$toast.open({
      duration: 5000,
      message: e.response.data.error,
      position: "is-bottom",
      type: "is-danger"
    });
  }
}
}
}
</script>

<style>
  body{
    padding-top: 48px;
  }
</style>
