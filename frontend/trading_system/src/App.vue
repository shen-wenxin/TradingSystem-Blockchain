<template>
  <v-app>
    <v-main>
      <router-view />
      <GlobalMsgbar></GlobalMsgbar>
    </v-main>
  </v-app>
</template>

<script>
import GlobalMsgbar from "./components/GlobalMsgbar/index.vue";
export default {
  name: "App",
  components: {
    GlobalMsgbar,
  },
};
</script>

<style scoped>
/* 全局淡灰色背景样式，勿删 */
#app {
  background-color: rgb(250, 250, 250);
}
</style>

<style>
.v-application .elevation-0 {
  box-shadow: rgba(0, 0, 0, 0.02) 0px 1px 3px 0px,
    rgba(27, 31, 35, 0.15) 0px 0px 0px 1px !important;
}
.v-application .elevation-1 {
  box-shadow: rgba(0, 0, 0, 0.05) 0px 6px 24px 0px,
    rgba(0, 0, 0, 0.08) 0px 0px 0px 1px !important;
}

.v-application .elevation-2 {
  box-shadow: rgba(0, 0, 0, 0.25) 0px 4px 8px -2px,
    rgba(0, 0, 0, 0.08) 0px 0px 0px 1px !important;
}
.v-application .elevation-3 {
  box-shadow: rgba(0, 0, 0, 0.16) 0px 10px 36px 0px,
    rgba(0, 0, 0, 0.06) 0px 0px 0px 1px !important;
}
.v-application .elevation-4,
.v-application .elevation-5,
.v-application .elevation-6,
.v-application .elevation-7,
.v-application .elevation-8,
.v-application .elevation-9,
.v-application .elevation-10,
.v-application .elevation-11,
.v-application .elevation-12,
.v-application .elevation-13,
.v-application .elevation-14,
.v-application .elevation-15,
.v-application .elevation-16,
.v-application .elevation-17,
.v-application .elevation-18,
.v-application .elevation-19,
.v-application .elevation-20,
.v-application .elevation-21,
.v-application .elevation-22,
.v-application .elevation-23,
.v-application .elevation-24 {
  box-shadow: rgba(0, 0, 0, 0.2) 0px 20px 30px,
    rgba(0, 0, 0, 0.06) 0px 0px 0px 1px !important;
}
</style>
