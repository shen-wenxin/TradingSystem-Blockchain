<template>
  <div>
    <v-container id="main-view">
      <header class="text-h4 my-6">我的商品列表</header>
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
            <v-btn
                color="orange lighten-2"
                text
                @click="showMessage"
            >
              Explore
            </v-btn>
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
                上架时间: {{good.time}}
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
import TimeService from "../../api/time/time";
import GlobalMessage from '../../components/GlobalMsgbar/api';
import ResponseExtractor from "../../utils/response-extractor";

export default {
  mounted() {
    this.getGoodList();

  },
  name: "GoodsOnSalePage",
  components:{

  },
  data () {
    return {
      goods :[]
    }


  },
  methods: {
    showMessage(){
      GlobalMessage.success("还没想好要搞什么功能噢")
    },
    getGoodList(){
      GoodService.getSaleGoodListByAccount().then((resp) =>{
        const goodsList = ResponseExtractor.getData(resp);
        console.log("getGoodList", goodsList);
        for(var i = 0;i < goodsList.length;i ++){
          var good = {
            "name": goodsList[i].name,
            "price": goodsList[i].price,
            "time": TimeService.timesampToTime(goodsList[i].lastUpdate),
            "showDetails": false
          }
          this.goods.push(good)
        }
      })
    }


  },
}
</script>

<style scoped>
#main-view {
  max-width: 800px;
}
</style>