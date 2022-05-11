<template>
  <div>
    <v-container id="main-view">
      <header class="text-h4 my-6">待售商品列表</header>
      <div
          v-for="(good, index) in goods"
          v-bind:key="index"
      >
        <v-card
            outlined
            elevation="0"
            class="my-4"
        >
          <v-card-title>{{  good.name }}</v-card-title>
          <v-card-subtitle>价格：{{  good.price }}(单位：RMB)</v-card-subtitle>
          <v-card-actions>
            <v-dialog
              v-model = "dialog"
              persistent
              max-width="290"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                    color="orange lighten-2"
                    text
                    v-bind="attrs"
                    v-on="on"
                    @click="choosegood(good.id)"
                >
                  点击购买
                </v-btn>
              </template>
              <v-card>
                <v-card-title class="text-h5">
                  请您确认购买信息
                </v-card-title>
                <v-card-text>名称: {{goodchose.name}}<br>价格: {{goodchose.price}}元 <br>发行者: {{goodchose.issuer}}
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn
                      color="green darken-1"
                      text
                      @click="dialog = false"
                  >
                    算了吧
                  </v-btn>
                  <v-btn
                      color="green darken-1"
                      text
                      @click="buyGood(index)"
                  >
                    好耶
                  </v-btn>
                </v-card-actions>
              </v-card>

            </v-dialog>

            <v-spacer></v-spacer>

            <v-btn
                icon
                @click="good.showDetails = !good.showDetails"
            >
              <v-icon>{{ good.showDetails ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
            </v-btn>

          </v-card-actions>
          <v-expand-transition>
            <div v-show="good.showDetails">
              <v-divider></v-divider>

              <v-card-text>
                上架时间: {{good.time}}<br>
              </v-card-text>
            </div>
          </v-expand-transition>

        </v-card>
      </div>
    </v-container>
  </div>
</template>

<script>

import GoodService from "../../api/goods/goods";
import ResponseExtractor from "../../utils/response-extractor";
import TimeService from "../../api/time/time";
import GlobalMessage from "../../components/GlobalMsgbar/api";
import store from "../../store";

export default {
  name: "BuyGoods",
  mounted() {
    this.getGoodList();
  },
  components:{

  },

  methods: {
    choosegood(index){
      console.log(index)
      this.goodchose = this.goods[index]
    },
    buyGood(index){
      console.log("index = " + index)
      var tradeTime = new Date().getTime();
      const buydata = {
        "tradeTime" : tradeTime,
        "price": this.goodchose.price,
        "buyerId": store.state.user.account,
        "salerId": this.goodchose.issuerId,
        "commodityId": this.goodchose.commodityId,
      }
      console.log(buydata)
      GoodService.buyGood(buydata).then((resp) => {
        const data = ResponseExtractor.getData(resp)
        console.log(data)
        GlobalMessage.success("购买申请已提交，请耐心等待，数据更新会有延迟，请稍后刷新查看")
      })
      this.dialog = false
      this.$router.push({ path :"/CusGoodsBeBaught"}).catch(err => console.log(err));
    },
    getGoodList(){
      GoodService.getAllSaleGoodList().then((resp) => {
        const goodsList = ResponseExtractor.getData(resp);
        console.log("getGoodList", goodsList);
        for(var i = 0;i < goodsList.length;i ++){
          var good = {
            "id": i,
            "name": goodsList[i].name,
            "price": goodsList[i].price,
            "time": TimeService.timesampToTime(goodsList[i].lastUpdate),
            "issuer": goodsList[i].issuerName,
            "issuerId": goodsList[i].issuerId,
            "commodityId": goodsList[i].id,
            "showDetails": false
          }
          this.goods.push(good)
        }
      })

    }
  },
  data () {
    return {
      goodchose: {},
      goodIndex: 0,
      dialog: false,
      goods :[],
      issuer: ""

    }
  }
}
</script>

<style scoped>
#main-view {
  max-width: 800px;
}
</style>