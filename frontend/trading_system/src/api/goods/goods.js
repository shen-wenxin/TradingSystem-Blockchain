// import HttpService from "../../config/axios";


import store from "../../store";
import HttpService from "../../config/axios";

const GoodService = {
    /**
     * 上架商品
     */
    saleGood(goodRegister){
        return HttpService({
            url: '/commodity/sale',
            method: 'post',
            data: {
                "name": goodRegister.name,
                "price": goodRegister.price,
                "issuerId": store.state.user.account,
                "issuerName": store.state.user.name
            }
          }
        )
    },

    /**
     * 得到上架的商品列表(通过account)
     * */
    getSaleGoodListByAccount(){
        const account =  store.state.user.account
        console.log("account", account)
        return HttpService({
            url: "/commodity/getOnSaleGoodInfo/" + account,
            method: 'get',
        })
    },

    /**
    * 得到所有上架的商品列表
    * */
    getAllSaleGoodList(){
        return HttpService({
            url: "/commodity/getAllOnSaleGoodInfo",
            method: 'get',
        })
    },

    /***
     * 购买商品
     */
    buyGood(buydata){
        return HttpService({
            url: "/trade/buy",
            method: 'post',
            data: {
                "tradeTime": buydata.tradeTime,
                "price": buydata.price,
                "buyerId":buydata.buyerId,
                "salerId":buydata.salerId,
                "commodityId":buydata.commodityId
            }
        })
    },

    /**
     * 得到所有已购买商品（with 消费者id）
     */
    getAllGoodBeBaught(){
        const account =  store.state.user.account
        return HttpService({
            url: "/commodity/getGoodBeBaught/" + account,
            method: "get"
        })
    },
    /**
     * 得到所有已售出商品列表(with 商家id）
     * */
    getAllGoodSaledByBus(){
        const account =  store.state.user.account
        return HttpService({
            url: "/commodity/getGoodSaled/" + account,
            method: "get"
        })
    }



}
export default GoodService;