<template>
  <div class="fullScreenHeight">
    <div class="fullScreenHeight" id="loginBackground">
      <v-card
        class="loginCard pa-6 mx-auto rounded-lg"
        max-width="600"
        elevation="4"
      >
        <v-container id="loginFormContainer">
          <v-form
            v-model="valid"
            ref="loginForm"
            lazy-validation
            class="loginFormContainer"
          >
            <div class="text-h3 my-2">登录</div>
            <p class="text-subtitle-1 grey--text">
              使用您的账户名和密码来登录系统
            </p>

            <v-row>
              <v-col>
                <v-text-field
                  v-model="user.account"
                  :rules="rules.basicRules"
                  label="用户名"
                  class="rounded-lg"
                  hide-details
                  rounded
                  filled
                  single-line
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col>
                <v-text-field
                  v-model="user.password"
                  :rules="rules.basicRules"
                  label="密码"
                  class="rounded-lg"
                  hide-details
                  rounded
                  filled
                  single-line
                  type="password"
                ></v-text-field>
              </v-col>
            </v-row>

            <!--登录/注册 按钮-->
            <v-row justify-md="end">
              <v-col cols="12" md="3">
                <v-btn
                  block
                  color="primary"
                  depressed
                  class="rounded-lg"
                  height="50px"
                  @click="submit"
                >
                  <v-icon>mdi-login-variant</v-icon>
                  <span class="mx-2">登录</span>
                </v-btn>
              </v-col>
              <v-col cols="12" md="3">
                <v-btn
                  block
                  outlined
                  depressed
                  class="rounded-lg"
                  height="50px"
                  @click="resetForm"
                >
                  <v-icon>mdi-account-plus</v-icon>
                  <span class="mx-2">注册</span>
                </v-btn>
              </v-col>
            </v-row>
          </v-form>
        </v-container>
      </v-card>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  components: {},
  data: () => ({
    valid: false,
    rememberMe: false,
    user: {
      account: "",
      password: "",
    },
    rules: {
      basicRules: [(v) => !!v || "Req uired"],
    },
    isAlertSeen: false,
    alertMessage: "",
    alertDialog: false,
  }),
  methods: {
    async submit() {
      if (this.$refs.loginForm.validate()) {
        await this.$store.dispatch("user/userLogin", this.user);
        await this.$store.dispatch("user/getInfo");
        await this.$store.dispatch(
          "user/generatePermittedRouteList",
          this.$store.state.user.role
        );
        this.$router.push({ path: "/" }).catch(err => (console.log(err)));
      }
    },
    resetForm() {
      this.$router.push({path: "/register"}).catch(err => (console.log(err)));
    },
  },
};
</script>

<style scoped>
.loginCard {
  position: relative;
  top: 50%;
  transform: translateY(-60%);
  background-color: rgb(255, 255, 255) !important;
}

.fullScreenHeight {
  height: 100%;
}

#loginBackground {
  /*background-image: url('../../assets/bg3.jpg');*/
  background-attachment: fixed;
  background-size: cover;
  background-repeat: no-repeat;
}
</style>
