import HttpService from "../../config/axios";
import store from "../../store";

const UserService = {
    /**
     * 发送注册请求
     * @param account 账户名
     * @returns {AxiosPromise}
     * **/

    register(user) {
        console.log("account", user.account)
        console.log("password", user.password)
        console.log("name", user.name)
        console.log("role", user.role)

        return HttpService({
            url: '/register',
            method: 'post',
            data: {
                "account": user.account,
                "password": user.password,
                "name": user.name,
                "role": user.role
            }
        })
    },


    /**
     * 发送第一次登录请求
     * @param account 账户名
     * @returns {AxiosPromise}
     */
    loginAccount(user) {
        return HttpService({
            url: '/login',
            method: 'post',
            data: {
                "account": user.account,
                "password": user.password
            },
        })
    },

    /**
     * 发送第二次登录请求
     * @param id 用户id
     * @param encryptedPassword 加密处理后的密码
     * @returns {AxiosPromise}
     */
    loginPwd(id, encryptedPassword) {
        return HttpService({
            url: '/usr/loginPwd',
            method: 'post',
            data: {
                id: id,
                password: encryptedPassword
            }
        })
    },

    /**
     * 获取用户名称
     * */
    getUserNameById(id) {
        return HttpService({
            url: '/usr/getUserNameById/' + id,
            method: 'get',
        })
    },

    /**
     * 发送登出请求
     * @returns {AxiosPromise}
     */
    logout() {
        return HttpService({
            url: '/logout',
            method: 'post'
        })
    },

    /**
     * 拉取用户信息
     * @returns {AxiosPromise}
     */
    getUserInfo() {
        return HttpService({
            url: '/usr/getUserInfo/',
            method: 'get',
        })
    },

    /**
     * 判断当前全局状态中是否有用户的全局信息
     * @param user 传入vuex中的user对象
     * @returns {boolean}
     */
    hasCompleteInfo(user) {
        if (!user.account || !user.name || !user.role) {
            return false;
        } else return true;
    },

    /**
     * 向后端发送请求，更改用户头像路径
     * @param avatarUrl 新头像路径（来自七牛云）
     * @returns {AxiosPromise}
     */
    updateUserAvatar(avatarUrl) {
        return HttpService({
            url: '/usr/changeAvatarPath',
            method: 'post',
            data: {
                avatarPath: avatarUrl
            }
        })
    },

    /**
     * 向后端发送请求，请求商家信息
     * */
    getBusInfo() {
        const account = store.state.user.account
        return HttpService({
            url: "/usr/getBusInfo/" + account,
            method: "get"
        })
    },
    //
    getBusMonthProfit() {
        const account = store.state.user.account
        return HttpService({
            url: "/usr/getBusMonthProfit/" + account,
            method: "get"
        })
    },
    getBusAccountList() {
        const account = store.state.user.account
        return HttpService({
            url: "/usr/getUserAccountList/" + account,
            method: "get"
        })
    },

    /**
     *  向后端发送请求，请求消费者信息
     */
    getCusInfo() {
        const account = store.state.user.account
        return HttpService({
            url: "/usr/getCusInfo/" + account,
            method: "get"
        })
    },
    getCusMonthSpent() {
        const account = store.state.user.account
        return HttpService({
            url: "/usr/getCusMonthSpent/" + account,
            method: "get"
        })
    },
    getCusAccountList() {
        const account = store.state.user.account
        return HttpService({
            url: "/usr/getUserAccountList/" + account,
            method: "get"
        })
    },

    /**
     * 向后端发送请求，请求所有商家信息
     */
    getAllBusInfo() {
        return HttpService({
            url: "/usr/getAllBusInfo",
            method: "get"
        })
    },

    /**
     * 向后端发送请求，请求所有用户信息
     */
    getAllCusInfo() {
        return HttpService({
            url: "/usr/getAllCusInfo",
            method: "get"

        })
    },

    /**
     * 向后端发送请求，请求所有商品信息
     */
    getAllCommodityInfo() {
        return HttpService({
            url: "/commodity/getAllCommodity",
            method: "get"
        })
    },

    /**
     * 向后端发送请求，请求所有交易信息
     */
    getAllTradeInfo() {
        return HttpService({
            url: "/trade/getAllTrade",
            method: "get"
        })
    }








}



export default UserService;