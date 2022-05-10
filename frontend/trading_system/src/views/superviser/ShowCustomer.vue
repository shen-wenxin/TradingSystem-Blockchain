<template>
  <div>
    <v-container id="main-view">
      <header class="text-h4 my-6">消费者信息总览</header>
      <div
          v-for="(cus, index) in customers"
          v-bind:key="index"
      >
        <v-card
            outlined
            elevation="0"
            class="my-4"
        >
          <v-card-title>{{  cus.name }}</v-card-title>
          <v-card-subtitle>联系方式：{{  cus.phone }}(电话)</v-card-subtitle>
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
                <v-card-text>名称: {{cus.name}}<br>电话: {{cus.phone}} <br>余额: {{cus.balance}}(卖出{{cus.commodityCount}}件宝贝)
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
                      @click="UnvalidCus(index)"
                  >
                    好耶
                  </v-btn>
                </v-card-actions>
              </v-card>

            </v-dialog>

            <v-spacer></v-spacer>

            <v-btn
                icon
                @click="cus.showDetails = !cus.showDetails"
            >
              <v-icon>{{ cus.showDetails ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
            </v-btn>

          </v-card-actions>
          <v-expand-transition>
            <div v-show="cus.showDetails">
              <v-divider></v-divider>

              <v-card-text>
                购买宝贝: {{cus.commodityCount}}件<br>
                余额:  {{cus.balance}} 元<br>
                账户状态:  {{cus.state}}<br>
                链上信息上次更新时间:  {{cus.lastupdateTime}}
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
  name: "ShowCustomer",
  mounted() {
    this.getCusList();
  },
  components:{

  },

  methods: {
      UnvalidCus(index){
        console.log(index)
        GlobalMessage.success("这功能还没做好，等下版本吧")
        this.dialog = false
      },
      getCusList(){
        UserService.getAllCusInfo().then((resp) => {
          const cusList = ResponseExtractor.getData(resp);
          console.log("cusList", cusList);
          for(var i = 0;i < cusList.length;i ++){
              var cus = {
                name: cusList[i].name,
                phone: cusList[i].phone,
                commodityCount: cusList[i].commodityCount,
                balance: cusList[i].balance,
                state: "有效的", // 预设 之后请更改
                lastupdateTime: TimeService.timesampToTime(cusList[i].lastUpdateTime),
                "showDetails": false
              }
              this.customers.push(cus)
          }
        })

      }

  },
  data () {
    return {
      dialog: false,
      customers :[],
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