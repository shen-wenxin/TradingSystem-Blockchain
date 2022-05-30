<template>
 <v-container class="main-cols-wrapper">
   <v-card
       class="pa-6 mx-auto rounded-lg"
       max-width="600"
       elevation="4"
   >
     <v-container>
       <v-form
           v-model="valid"
           ref="goodsFrom"
           lazy-validation
       >
         <div class="text-h3 my-2">上架商品</div>
         <p class="text-subtitle-1 grey--text">
           请真实填写商品信息
         </p>

         <v-row>
           <v-col>
             <v-text-field
                 v-model="goodRegister.name"
                 :rules="rules.basicRules"
                 label="请输入商品名称"
                 class="rounded-lg"
                 hide-details
                 rounded
                 filled
                 single-line
             ></v-text-field>
           </v-col>
         </v-row>
         <v-row>
           <v-col>
             <v-text-field
                 v-model="goodRegister.price"
                 :rules="rules.basicRules"
                 label="请商品价格"
                 class="rounded-lg"
                 hide-details
                 rounded
                 filled
                 single-line
             ></v-text-field>
           </v-col>
         </v-row>
         <v-row justify-md="end">
           <v-col cols="12" md="3">
             <v-btn
                 block
                 outlined
                 depressed
                 class="rounded-lg"
                 height="50px"
                 @click="salegood"
             >
               <v-icon>mdi-account-plus</v-icon>
               <span class="mx-2">上架</span>
             </v-btn>
           </v-col>
         </v-row>
       </v-form>
     </v-container>
   </v-card>

 </v-container>
</template>

<script>
import GoodService from "../../api/goods/goods";
import GlobalMessage from "../../components/GlobalMsgbar/api";
import ResponseExtractor from "../../utils/response-extractor";

export default {
  name: "SalesGood",
  components: {},
  data: () => ({
    valid: false,
    goodRegister: {
      name: "",
      price :"",
    },
    rules: {
      basicRules: [(v) => !!v || "Req uired"],
    },
  }),
  methods: {
    async salegood(){
      if (this.$refs.goodsFrom.validate()){

        var price = this.goodRegister.price
        console.log(price)
        if (Number(price)){
          console.log("is price")
            GoodService.saleGood(this.goodRegister).then((resp) => {
            const data = ResponseExtractor.getData(resp);
            GlobalMessage.success("商品上架成功");
            console.log("salegood response result", data);
            this.$router.push({ path :"/goodsOnSale"}).catch(err => console.log(err));
          }).catch((error) => {
            console.log("register failed", error);
          })
        }else{
            GlobalMessage.error("请输入正确价格:(");


        }



      }
    }

  },
};
</script>



