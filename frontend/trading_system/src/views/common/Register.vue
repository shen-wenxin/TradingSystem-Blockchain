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
              ref="registerFrom"
              lazy-validation
              class="loginFormContainer"
          >
            <div class="text-h3 my-2">注册</div>
            <p class="text-subtitle-1 grey--text">
              请注册您的TS账户
            </p>

            <v-row>
              <v-col>
                <v-text-field
                    v-model="userRegister.name"
                    :rules="rules.basicRules"
                    label="请输入姓名"
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
                    v-model="userRegister.account"
                    :rules="rules.basicRules"
                    label="请输入手机号"
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
                    v-model="userRegister.password"
                    :rules="rules.basicRules"
                    label="请输入密码"
                    class="rounded-lg"
                    hide-details
                    rounded
                    filled
                    single-line
                    type="password"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row>
              <v-col>
                <v-select
                  :options="roleItems"
                  :items="roleItems"
                  v-model="userRegister.role"
                  item-text="value"
                  item-value = "id"
                  label="请选择身份"
                  filled
                ></v-select>

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
                    @click="getToLogin"
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
                    @click="register"
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
import GlobalMessage from "../../components/GlobalMsgbar/api";
import UserService from "../../api/auth/user";
import ResponseExtractor from "../../utils/response-extractor";

export default {
  name: "Login",
  components: {},
  data: () => ({
    valid: false,
    rememberMe: false,
    userRegister: {
      name :"",
      account: "",
      password: "",
      role: 0
    },
    rules: {
      basicRules: [(v) => !!v || "Req uired"],
    },
    isAlertSeen: false,
    alertMessage: "",
    alertDialog: false,
    roleItems :[
      {
        "id":1,
        "value": "监管人员"
      },
      {
        "id":2,
        "value": "商家"
      },
      {
        "id":3,
        "value": "消费者"
      },

    ]
  }),
  methods: {

    getToLogin(){
      // 返回登录界面
      this.$router.push({path: "/login"}).catch(err => (console.log(err)))
    },
    async register() {
      // 注册
      if (this.$refs.registerFrom.validate()){
        console.log("this.userRegister :",this.userRegister.name)
        UserService.register(this.userRegister).then((resp) => {
          const data = ResponseExtractor.getData(resp);
          GlobalMessage.success("注册申请已提交，请耐心等待")
          console.log("register response result", data);
          this.$router.push({ path :"/login"}).catch(err => console.log(err));
        })
        .catch((error) => {
          console.log("register failed", error);
        })

      }
    },
    async submit() {
      if (this.$refs.registerFrom.validate()) {
        await this.$store.dispatch("user/userLogin", this.user);
        await this.$store.dispatch("user/getInfo", this.user);
        await this.$store.dispatch(
            "user/generatePermittedRouteList",
            this.$store.state.user.role
        );
        this.$router.push({ path: "/" }).catch(err => (console.log(err)));
      }
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
