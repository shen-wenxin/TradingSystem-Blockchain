<template>
  <div>
    <v-container id="main-view">
      <header class="text-h4 my-6">已购买商品</header>
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
                  点击退货
                </v-btn>
              </template>
              <v-card>
                <v-card-title class="text-h5">
                  请您确认退货信息
                </v-card-title>
                <v-card-text>名称: {{choose.name}}<br>价格: {{choose.price}}元 <br>发行者: {{choose.issuer}}
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
                      @click="returnGood(good.id)"
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

export default {
  name: "GoodsBeBaught",
  mounted() {
    this.getGoodList();
  },
  components:{

  },

  methods: {
    async choosegood(index){
      console.log("get in choose good")
      this.choose = this.goods[index]
      console.log(this.choose)
    },
    async returnGood(){
      console.log("the index is:")
      console.log(this.choose.id)
      GlobalMessage.success("该功能还未开放哦")
      this.dialog = false
    },
    getGoodList(){
      GoodService.getAllGoodBeBaught().then((resp) => {
        const goodsList = ResponseExtractor.getData(resp);
        console.log("getGoodList", goodsList);
        for(var i = 0;i < goodsList.length;i ++){
          console.log("i = " +i)
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
      goodIndex: 0,
      dialog: false,
      goods :[],
      choose : {},
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