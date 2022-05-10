<template>
  <div>
    <v-snackbar top
                dark
                v-model="visible"
                :color="color"
    >

      {{ msg }}

      <template v-slot:action="{ attrs }">
        <v-btn
            v-if="showClose"
            text
            @click="close"
            v-bind="attrs"
            icon
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </template>



    </v-snackbar>
  </div>
</template>

<script>
export default {
  name: "GlobalMsgbar",
  computed: {
    visible() {
      return this.$store.state.GlobalMsgbar.visible
    },
    showClose() {
      return this.$store.state.GlobalMsgbar.showClose
    },
    color() {
      return this.$store.state.GlobalMsgbar.color
    },
    msg() {
      return this.$store.state.GlobalMsgbar.msg
    }
  },
  methods: {
    close() {
      this.$store.commit('GlobalMsgbar/CLOSE_SNACKBAR')
    }
  }
}
</script>

<style scoped>

</style>