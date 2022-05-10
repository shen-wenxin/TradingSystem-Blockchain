import Vue from 'vue'
import Vuex from 'vuex'
import GlobalMsgbar from "../components/GlobalMsgbar/index.js";
import user from './modules/user'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {},
    mutations: {},
    actions: {},
    modules: {
        GlobalMsgbar,
        user
    }
})
