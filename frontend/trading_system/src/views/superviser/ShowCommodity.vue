<template>
  <div>
    <v-container id="main-view">
      <header class="text-h4 my-6">商品信息总览</header>
      <div
          v-for="(com, index) in commodityList"
          v-bind:key="index"
      >
        <v-card
            outlined
            elevation="0"
            class="my-4"
        >
          <v-card-title>{{  com.name }}</v-card-title>
          <v-card-subtitle>价格：{{  com.price }} (RMB)</v-card-subtitle>
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
                <v-card-text>名称: {{com.name}}<br>价格: {{com.price}} <br>商家: {{com.issuername}}<br> 拥有者: {{com.owerName}})
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
                      @click="UnvalidCom(index)"
                  >
                    好耶
                  </v-btn>
                </v-card-actions>
              </v-card>

            </v-dialog>

            <v-spacer></v-spacer>

            <v-btn
                icon
                @click="com.showDetails = !com.showDetails"
            >
              <v-icon>{{ com.showDetails ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
            </v-btn>

          </v-card-actions>
          <v-expand-transition>
            <div v-show="com.showDetails">
              <v-divider></v-divider>

              <v-card-text>
                商家: {{com.issuername}}<br>
                拥有者:  {{com.ownerName}} <br>
                状态:  {{com.state}}<br>
                链上信息上次更新时间:  {{com.lastupdateTime}}
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
  name: "ShowCommodity",
  mounted() {
    this.getCommodityList();
  },
  components:{

  },

  methods: {
      UnvalidCom(index){
        console.log(index)
        GlobalMessage.success("这功能还没做好，下版本也别想做好了")
        this.dialog = false
      },
      getCommodityList(){
        UserService.getAllCommodityInfo().then((resp) => {
          const comList = ResponseExtractor.getData(resp);
          console.log("comList", comList);
          for(var i = 0;i < comList.length;i ++){
              var com = {
                name: comList[i].name,
                price: comList[i].price,
                issuername: comList[i].issuerName,
                ownerName: comList[i].ownerName,
                state: "有效的",
                lastupdateTime: TimeService.timesampToTime(comList[i].lastUpdate),
                "showDetails": false
              }
              this.commodityList.push(com)
          }
        })

      }

  },
  data () {
    return {
      dialog: false,
      commodityList :[],

    }
  }
}
</script>

<style scoped>
#main-view {
  max-width: 800px;
}
</style>