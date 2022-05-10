import router from "../router/index";
import store from "../store";
import { getLocalToken } from "../utils/token";
import UserService from "../api/auth/user";

router.beforeEach(async(to, from, next) => {
    const token = getLocalToken();
    console.log("Begin to getLocalToekn and the token is" + token);
    /**
     * 请勿改动此部分逻辑
     * 简单说明：
     * 1.首先简单粗暴地通过token有无来判断用户是否需要直接重新登录
     * 2.若有token，判断全局状态中是否有完整信息，有就放行，没有就拉取
     * 3.拉取信息之后，放行
     */
    if (token) {
        console.log("has token");
        console.log("store.state.user:")
        console.log(store.state.user)
        if (UserService.hasCompleteInfo(store.state.user)) {
            console.log("has complete info");
            next();
        } else {
            console.log("gethere")
            try {
                await store.dispatch("user/getInfo");
                await store.dispatch(
                    "user/generatePermittedRouteList",
                    store.state.user.role
                );
                /**
                 * 此处是next({...}})而不是next()
                 * 是为了处理用户手动刷新浏览器页面后什么都不显示的情况
                 * 按照正常逻辑，用户手动刷新后，vuex的数据会清空，但token还在，必然需要重新拉取一次用户数据
                 * 与此同时状态里的路由表也会清空，所以我们在拉取用户信息之后重新生成路由表
                 * 同时也因为路由表清空，我们需要重新addRoutes()，但它需要时间，此时next()若在addRoutes()尚未完成时执行，
                 * 就会导致进入空页面
                 * 此处通过next({...}})可以重新再走一次导航守卫，确保addRoutes已经完成
                 */
                next({...to, replace: true });
            } catch (error) {
                console.log("intercepter error", error);
                await store.dispatch("user/resetToken");
                next("/login");
            }
        }
    } else {
        if (to.path === "/login" || to.path === "/register") {
            next();
        } else {
            next("/login");
        }
    }
});