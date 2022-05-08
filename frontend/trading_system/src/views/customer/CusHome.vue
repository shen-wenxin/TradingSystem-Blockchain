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
                <div class="text--secondary">{{account}}</div>
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
                <div class="text--secondary mt-2 mb-4">每月收益</div>
                <template>
                  <v-sparkline
                      :value="value"
                      :gradient="gradient"
                      :smooth="radius || false"
                      :padding="padding"
                      :line-width="width"
                      :stroke-linecap="lineCap"
                      :gradient-direction="gradientDirection"
                      :fill="fill"
                      :type="type"
                      :auto-line-width="autoLineWidth"
                      auto-draw
                  ></v-sparkline>
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
const gradients = [
  ['#222'],
  ['#444343'],
  ['red', 'orange', 'yellow'],
  ['purple', 'violet'],
  ['#806780', '#7b6969', '#363434'],
  ['#6d6d6d', '#525152', '#000000'],
]

export default {
  name: "CusHome",
  data() {
    return {
      width: 2,
      radius: 10,
      padding: 8,
      lineCap: 'round',
      gradient: gradients[5],
      value: [0, 2, 5, 9, 5, 10, 3, 5, 0, 0, 1, 8, 2, 9, 0],
      gradientDirection: 'top',
      gradients,
      fill: false,
      type: 'trend',
      autoLineWidth: false,

      username: "Susan",
      account: "1821234567",
      salesnum: "60",
      pricesum: "100",
      pricemonth: "60",
      IncomeDetails: [
        {
          month: "5",
          income: "20",
          total: "100",
        },
        {
          month: "4",
          income: "30",
          total: "80"
        },
        {
          month: "3",
          income: "50",
          total: "50"

        }
      ],
      goodsSaled: [
        {
          name: '苹果',
          time: '2021-09-01 23:59:59',
          price: "100",
          owner: "tom",
          showDetails: false
        },
        {
          name: '操作系统',
          time: '2021-09-09 23:59:59',
          price: "100",
          owner: "tom",
          showDetails: false
        },
        {
          name: '计算机网络',
          time: '2021-12-01 23:59:59',
          price: "100",
          owner: "tom",
          showDetails: false
        }
      ],

    }
  },
  methods: {}
}
</script>

<style scoped>
.main-wrapper {
  max-width: 1200px;
}
</style>