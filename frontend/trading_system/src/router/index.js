import Vue from "vue";
import VueRouter from "vue-router";
import Nav from "../views/common/Nav";
import Login from "../views/common/Login";
import PageNotFound from "../views/common/PageNotFound";
import Register from "../views/common/Register";
import GoodsOnSale from "../views/bussiness/GoodsOnSale";
import SaleGoods from "../views/bussiness/SaleGoods";
import GoodsSaled from "../views/bussiness/GoodsSaled";
import BusHome from "../views/bussiness/BusHome";
import CusHome from "../views/customer/CusHome";
import BuyGoods from "../views/customer/BuyGoods";
import GoodsBeBaught from "../views/customer/GoodsBeBaught";
import ShowBussiness from "../views/superviser/ShowBussiness";
import ShowCustomer from "../views/superviser/ShowCustomer";
import ShowCommodity from "../views/superviser/ShowCommodity";
import ShowTrade from "../views/superviser/ShowTrades"
Vue.use(VueRouter);

export const defaultRoutes = [{
        path: "/login",
        name: "Login",
        component: Login,
    },
    {
        path: "/register",
        name: "Register",
        component: Register
    },
    {
        path: "/404",
        name: "NotFoundPage",
        component: PageNotFound,
    },
];

export const asyncRoutes = [{
        path: "/",
        name: "App",
        component: Nav,
        meta: {
            roles: [1, 2, 3],
        },
        children: [{
                path: "BusHome",
                name: "BussinessHomePage",
                components: {
                    main: BusHome,
                },
                meta: {
                    nav: {
                        name: "主页"
                    },
                    roles: [2]
                }
            },
            {
                path: "goodsOnSale",
                name: "BusGoodsOnSale",
                components: {
                    main: GoodsOnSale,
                },
                meta: {
                    nav: {
                        name: "待售商品",
                    },
                    roles: [2],
                },
            },
            {
                path: "SaleGoods",
                name: "BusSaleGoods",
                components: {
                    main: SaleGoods,
                },
                meta: {
                    nav: {
                        name: "上架商品",
                    },
                    roles: [2],
                },
            },
            {
                path: "GoodsSaled",
                name: "BusGoodSaled",
                components: {
                    main: GoodsSaled,
                },
                meta: {
                    nav: {
                        name: "已售商品"
                    },
                    roles: [2],
                }
            },
            {
                path: "CusHome",
                name: "CustomerHomePage",
                components: {
                    main: CusHome,
                },
                meta: {
                    nav: {
                        name: "主页"
                    },
                    roles: [3]
                }
            },
            {
                path: "CusBuyGoods",
                name: "CustomerBuyGoods",
                components: {
                    main: BuyGoods,
                },
                meta: {
                    nav: {
                        name: "购买商品"
                    },
                    roles: [3]
                }
            },
            {
                path: "CusGoodsBeBaught",
                name: "CustomerGoodsBeBaught",
                components: {
                    main: GoodsBeBaught,
                },
                meta: {
                    nav: {
                        name: "已购买"
                    },
                    roles: [3]
                }
            },
            {
                path: "BusinessInfo",
                name: "ShowBusinessInfo",
                components: {
                    main: ShowBussiness,
                },
                meta: {
                    nav: {
                        name: "商家信息"
                    },
                    roles: [1]
                }
            },
            {
                path: "CustomerInfo",
                name: "ShowCustomerInfo",
                components: {
                    main: ShowCustomer
                },
                meta: {
                    nav: {
                        name: "买家信息"
                    },
                    roles: [1]
                }

            },
            {
                path: "CommodityInfo",
                name: "ShowCommodityInfo",
                components: {
                    main: ShowCommodity
                },
                meta: {
                    nav: {
                        name: "商品信息"
                    },
                    roles: [1]
                }
            },
            {
                path: "TradeInfo",
                name: "ShowTradeInfo",
                components: {
                    main: ShowTrade
                },
                meta: {
                    nav: {
                        name: "交易信息"
                    },
                    roles: [1]
                }

            }
        ],
    },
    {
        path: "*",
        redirect: "/404",
    },
];

const createRouter = () =>
    new VueRouter({
        // mode: 'history', // require service support
        mode: "hash",
        base: process.env.BASE_URL,
        routes: defaultRoutes,
        scrollBehavior: () => ({ y: 0 }),
    });

const router = createRouter();

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
    const newRouter = createRouter();
    router.matcher = newRouter.matcher; // reset router
}

export default router;