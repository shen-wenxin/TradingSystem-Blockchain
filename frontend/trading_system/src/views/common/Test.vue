<template>
  <div>
    <v-row class="ma-3" justify="start">
      <v-col cols="auto">
        <v-btn @click="warnMsg" color="warning">测试全局消息提示</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn @click="infoMsg" color="info">测试全局消息提示</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn @click="errorMsg" color="error">测试全局消息提示</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn @click="successMsg" color="success">测试全局消息提示</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn @click="testToken">获取token</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn @click="getUserInfo">获取用户信息</v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn @click="testPasswordDigest">测试密码摘要</v-btn>
      </v-col>
    </v-row>
    <v-row class="mx-4" justify="start">
      <v-col v-for="(card, index) in cards"
             :key="index" cols="auto">
        <v-card
            class="mx-auto mt-5 mb-12 px-6 py-6"
            max-width="400"
            :elevation="card.elevation"

        >
          <v-row align="center">
            <v-col cols="auto">
              <v-avatar size="64" rounded>
                <img
                    src="../../assets/avatar.jpg"
                    alt="Coiggahou"
                >
              </v-avatar>
            </v-col>
            <v-col cols="auto">
              <div class="text-h5">elevation-{{ card.name }}</div>
              <div class="caption">Coiggahou2002</div>
            </v-col>
          </v-row>
        </v-card>
      </v-col>

    </v-row>
    <v-row class="mx-4">
      <v-card
          class="mx-auto mt-5 mb-12 px-6 py-6"
          elevation="1"

      >
<!--        <MonacoEditor-->
<!--            class="editor"-->
<!--            v-model="code"-->
<!--            language="javascript"-->
<!--        />-->
      </v-card>

    </v-row>


  </div>
</template>

<script>

import UserService from "../../api/auth/user";
import PasswordService from "../../api/auth/password";

export default {
  name: "Test",
  components: {
    //MonacoEditor
  },
  data () {
    return {
      code: 'const noop = () => {}',
      cards: [
        {
          name: '0',
          elevation: 0
        },
        {
          name: '1',
          elevation: 1
        },
        {
          name: '2',
          elevation: 2
        },
        {
          name: '3',
          elevation: 3
        },
        {
          name: '4 及以上',
          elevation: 4
        }
      ],
      user: {
        username: 'root',
        password: 'hitszoj201',
      }

    }
  },
  methods: {
    errorMsg() {
      this.$message.error('This sis a error message');
    },
    warnMsg() {
      this.$message.warning('This is a error message');
    },
    infoMsg() {
      this.$message.info('This is a error message');
    },
    successMsg() {
      this.$message.success('This is a error message');
    },
    testToken() {
      this.$store.dispatch('user/userLogin', this.user);
    },
    getUserInfo() {
      UserService.getUserInfo().then(resp => {
        console.log(resp);
      })
    },
    testPasswordDigest() {
      let password = '123456';
      let random = 'abcd';
      let enc = PasswordService.encryptPassword(password, random);
      console.log(enc);
    },
  }
}
</script>

<style scoped>

</style>