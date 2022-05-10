<template>
  <div>
    <v-container id="main-view">
      <header class="text-h4 my-6">交易信息总览</header>
      <div
          v-for="(tra, index) in trades"
          v-bind:key="index"
      >
        <v-card
            outlined
            elevation="0"
            class="my-4"
        >
          <v-card-title>金额: {{  tra.price }}</v-card-title>
          <v-card-subtitle>交易时间: {{  tra.time }}</v-card-subtitle>
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
                >
                  操作他
                </v-btn>
              </template>
              <v-card>
                <v-card-title class="text-h5">
                  请您确定要把TA ban了吗
                </v-card-title>
                <v-card-text>id: {{tra.id}}<br>金额: {{tra.price}} <br>时间: {{tra.time}}<br>
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
                      @click="UnvalidTrade(index)"
                  >
                    好耶
                  </v-btn>
                </v-card-actions>
              </v-card>

            </v-dialog>

            <v-spacer></v-spacer>

            <v-btn
                icon
                @click="tra.showDetails = !tra.showDetails"
            >
              <v-icon>{{ tra.showDetails ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
            </v-btn>

          </v-card-actions>
          <v-expand-transition>
            <div v-show="tra.showDetails">
              <v-divider></v-divider>

              <v-card-text>
                买家id: {{tra.buyerId}}<br>
                卖家id:  {{tra.salerId}} <br>
                状态:  {{tra.state}}<br>
                链上信息上次更新时间:  {{tra.lastupdateTime}}
              </v-card-text>
            </div>
          </v-expand-transition>

        </v-card>
      </div>
    </v-container>
  </div>
</template>

<script>

import ResponseExtractor from "../../utils/response-extractor";
import TimeService from "../../api/time/time";
import GlobalMessage from "../../components/GlobalMsgbar/api";
import UserService from '../../api/auth/user';

export default {
  name: "ShowTrade",
  mounted() {
    this.getTradeList();
  },
  components:{

  },

  methods: {
      UnvalidTrade(index){
        console.log(index)
        GlobalMessage.success("这功能还没做好，等下版本吧")
        this.dialog = false
      },
      getTradeList(){
        UserService.getAllTradeInfo().then((resp) => {
          const traList = ResponseExtractor.getData(resp);
          console.log("traList", traList);
          for(var i = 0;i < traList.length;i ++){
              var tra = {
                id: traList[i].tradeId,
                time: TimeService.timesampToTime(traList[i].tradeTime),
                price: traList[i].price,
                buyerId: traList[i].buyerId,
                salerId: traList[i].salerId,
                state: "有效的", // 预设 之后请更改
                lastupdateTime: TimeService.timesampToTime(traList[i].lastUpdateTime),
                "showDetails": false
              }
              this.trades.push(tra)
          }
        })

      }

  },
  data () {
    return {
      dialog: false,
      trades :[],
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