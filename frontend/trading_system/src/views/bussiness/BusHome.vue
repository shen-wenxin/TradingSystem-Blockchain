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
                  <span class="text--secondary mx-2">已售出 {{salesnum}} 件宝贝</span>
                </div>
                <div class="ma-4">
                  <v-icon>mdi-cash-100</v-icon>
                  <span class="text--secondary mx-2">共盈利 {{pricesum}} 元</span>
                </div>
                <div class="ma-4">
                  <v-icon>mdi-calendar-month</v-icon>
                  <span class="text--secondary mx-2">该月盈利 {{pricemonth}} 元</span>
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
                      v-for="(income, index) in IncomeDetails"
                      :key="index"
                      class="my-2"
                  >
                    <div class="text-h6">{{ income.month}} 月</div>
                    <div class="text--secondary">收入: {{income.income}}</div>
                    <div class="text--primary">收入总计: {{ income.total}} 元</div>
                    <v-divider class="my-4" v-if="index !== income.length - 1"/>
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
import TimeService from '../../api/time/time'
import ResponseExtractor from '../../utils/response-extractor'


export default {

  name: "BusHome",
  mounted(){
    // this.getBusInfo()
    this.getBusInfofake()
  },
  data() {
    return {
      username: "Susan",
      phone: "1821234567",
      salesnum: "60",
      pricesum: "100",
      pricemonth: "60",
      IncomeDetails: [],

    }
  },
  methods: {
    getBusInfo(){
      UserService.getBusInfo().then((resp) => {
        const bus = ResponseExtractor.getData(resp)
        console.log("bus",bus)
        this.username = bus.name
        this.phone = bus.phone
        this.salesnum = bus.commodityCount
        this.pricesum = bus.balance
        UserService.getBusMonthProfit().then((resp) => {
          const profit = ResponseExtractor.getData(resp)
          this.pricemonth = profit
          var acc = {
            month: TimeService.nowTime().month,
            income : this.pricemonth,
            total: this.pricesum
          }
            this.IncomeDetails.push(acc)
        })
        
      })
      UserService.getBusAccountList().then((resp) => {
        const accountList = ResponseExtractor.getData(resp)
        this.IncomeDetails.push(...accountList)
      })
    },
    getBusInfofake(){
        this.pricemonth = "10"
        this.username = "busnissman"
        this.phone = "12345678910"
        this.salesnum = "3"
        this.pricesum = "100"
        this.IncomeDetails = [{
          "month":"5",
          "income":10,
          "total":100
        },
        {
          "month":"4",
          "income":20,
          "total":90
        },
        {
          "month":"3",
          "income":70,
          "total":70
        },
        ]
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