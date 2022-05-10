<template>
  <div>
    <v-container id="main-view">
      <header class="text-h4 my-6">商家信息总览</header>
      <div
          v-for="(bus, index) in business"
          v-bind:key="index"
      >
        <v-card
            outlined
            elevation="0"
            class="my-4"
        >
          <v-card-title>{{  bus.name }}</v-card-title>
          <v-card-subtitle>联系方式：{{  bus.phone }}(电话)</v-card-subtitle>
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
                  请您确定扼杀ta吗
                </v-card-title>
                <v-card-text>名称: {{bus.name}}<br>电话: {{bus.phone}} <br>总盈利: {{bus.balance}}(卖出{{bus.commodityCount}}件宝贝)
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
                      @click="UnvalidBus(index)"
                  >
                    好耶
                  </v-btn>
                </v-card-actions>
              </v-card>

            </v-dialog>

            <v-spacer></v-spacer>

            <v-btn
                icon
                @click="bus.showDetails = !bus.showDetails"
            >
              <v-icon>{{ bus.showDetails ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
            </v-btn>

          </v-card-actions>
          <v-expand-transition>
            <div v-show="bus.showDetails">
              <v-divider></v-divider>

              <v-card-text>
                卖出宝贝:  {{bus.commodityCount}}件<br>
                盈利:  {{bus.balance}} 元<br>
                账户状态:  {{bus.state}}<br>
                链上信息上次更新时间:  {{bus.lastupdateTime}}
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
  name: "ShowBussiness",
  mounted() {
    this.getBusList();
  },
  components:{

  },

  methods: {
      UnvalidBus(index){
        console.log(index)
        GlobalMessage.success("这功能还没做好，等下版本吧")
        this.dialog = false
      },
      getBusList(){
        UserService.getAllBusInfo().then((resp) => {
          const busList = ResponseExtractor.getData(resp);
          console.log("busList", busList);
          for(var i = 0;i < busList.length;i ++){
              var bus = {
                name: busList[i].name,
                phone: busList[i].phone,
                commodityCount: busList[i].commodityCount,
                balance: busList[i].balance,
                state: "有效的",// TODO 预设 之后请修改
                lastupdateTime: TimeService.timesampToTime(busList[i].lastUpdateTime),
                "showDetails": false
              }
              this.business.push(bus)
          }
        })

      }

  },
  data () {
    return {
      dialog: false,
      business :[],
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