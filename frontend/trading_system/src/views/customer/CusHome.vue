<template>
  <div>
    <v-container class="mt-4 px-4 main-wrapper">
      <div>
        <v-row class="my-4">

          <!--个人信息面板-->
          <v-col cols="3">
            <v-card
                class="mx-auto px-2 py-3"
                elevation="1"
            >
              <!--头像-->
              <section class="d-flex flex-column align-center my-4">
                <v-avatar size="100">
                  <img
                      src="../../assets/avatar.jpg"
                      alt="Coiggahou"
                  >
                </v-avatar>
                <div class="text-h5 mt-6">{{username}}</div>
                <div class="text--secondary">{{phone}}</div>
              </section>
              <v-divider/>

              <!--基本信息-->
              <section class="py-2">
                <div class="ma-4">
                  <v-icon>mdi-gift</v-icon>
                  <span class="text--secondary mx-2">已购买 {{salesnum}} 件宝贝</span>
                </div>
                <div class="ma-4">
                  <v-icon>mdi-cash-100</v-icon>
                  <span class="text--secondary mx-2">资金还剩 {{pricesum}} 元</span>
                </div>
                <div class="ma-4">
                  <v-icon>mdi-calendar-month</v-icon>
                  <span class="text--secondary mx-2">该月消费 {{pricemonth}} 元</span>
                </div>
              </section>
              <v-divider/>

              <!--通过题目进度条展示-->
              <section class="d-flex flex-column align-center my-4">
                <div class="text--secondary mt-2 mb-4">笑一笑</div>
                <template>
                  <div class="laughword">
                  <p class="font-weight-light">
                      人生在世,谁能比得上商人那样逍遥富乐呢?
                  </p>
                  </div>
                  
                </template>
              </section>
            </v-card>
          </v-col>

          <v-col cols="8">

            <div class="mx-2 mb-8">

              <v-card elevation="1" class="">
                <v-progress-linear
                    color="light-blue"
                    height="3"
                    value="100"
                    striped
                ></v-progress-linear>
                <section class="py-3 px-4">
                  <div class="text-h5 mt-3 mb-7 light-blue--text">
                    <v-icon color="light-blue">mdi-handshake</v-icon>
                    收入明细
                  </div>
                  <div
                      v-for="(outcome, index) in OutComeDetails"
                      :key="index"
                      class="my-2"
                  >
                    <div class="text-h6">{{ outcome.month}} 月</div>
                    <div class="text--secondary">支出: {{outcome.outcome}}</div>
                    <div class="text--primary">当前剩余: {{ outcome.total}} 元</div>
                    <v-divider class="my-4" v-if="index !== outcome.length - 1"/>
                  </div>
                </section>
              </v-card>
            </div>

          </v-col>

        </v-row>
      </div>
    </v-container>
  </div>
</template>

<script>
import UserService from '../../api/auth/user'
import ResponseExtractor from '../../utils/response-extractor'
import TimeService from '../../api/time/time'


export default {
  name: "CusHome",
  mounted(){
    this.getCusInfo()
  },
  data() {
    return {
      username: "Susan",
      phone: "1821234567",
      salesnum: "60",
      pricesum: "100",
      pricemonth: "60",
      OutComeDetails: [],

    }
  },
  methods: {
    getCusInfo(){
      UserService.getCusInfo().then((resp) => {
        const cus = ResponseExtractor.getData(resp);
        console.log(cus)
        this.username = cus.name
        this.phone = cus.phone
        this.salesnum = cus.commodityCount
        this.pricesum = cus.balance
        UserService.getCusMonthSpent().then((resp) => {
          const spent = ResponseExtractor.getData(resp)
          this.pricemonth = spent
          var spe = {
            month: TimeService.nowTime().month,
            outcome : this.pricemonth,
            total: this.pricesum
          }
          this.OutComeDetails.push(spe)
        })
      })
      UserService.getBusAccountList().then((resp) => {
        const accountList = ResponseExtractor.getData(resp)
        this.OutComeDetails.push(...accountList)
      })
    }
  }
}
</script>

<style scoped>
.main-wrapper {
  max-width: 1200px;
}
.laughword{
  max-width: 140px;
}
</style>