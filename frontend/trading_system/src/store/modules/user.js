// import PasswordService from "../../api/auth/password";
import { removeLocalToken, setLocalToken } from "../../utils/token";
import ResponseExtractor from "../../utils/response-extractor";
import { filterAsyncRoutes } from "../../api/auth/permission";
import router, { asyncRoutes, defaultRoutes, resetRouter } from "../../router";
import UserService from "../../api/auth/user";

const user = {
  namespaced: true,

  state: {
    account: "",
    name: "",
    role: null,
    permittedRouteList: [],
  },

  mutations: {
    SET_USERNAME: (state, username) => {
      state.name = username;
    },
    REMOVE_USERNAME: (state) => {
      state.name = null;
    },
    SET_ACCOUNT: (state, account) => {
      state.account = account;
    },
    REMOVE_ACCOUNT: (state) => {
      state.account = null;
    },
    SET_TOKEN: (state, token) => {
      state.token = token;
    },
    REMOVE_TOKEN: (state) => {
      state.token = null;
    },
    SET_ROLE: (state, role) => {
      state.role = role;
    },
    REMOVE_ROLE: (state) => {
      state.role = null;
    },
    SET_ROUTE_LIST: (state, permittedRouteList) => {
      state.permittedRouteList = permittedRouteList;
    },
    REMOVE_ROUTE_LIST: (state) => {
      state.permittedRouteList = [];
    },
  },

  actions: {
    userLogin({ commit }, user) {
      return new Promise((resolve, reject) => {
        UserService.loginAccount(user)
          .then((resp) => {
            commit("SET_ACCOUNT", user.account);
            const data = ResponseExtractor.getData(resp);
            const token = data.token;
            setLocalToken(token);
            commit("SET_TOKEN", token);
            resolve();
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
    userLogout({ commit }) {
      return new Promise((resolve, reject) => {
        UserService.logout()
          .then(() => {
            commit("REMOVE_ACCOUNT");
            commit("REMOVE_USERNAME");
            commit("REMOVE_ROLE");
            commit("REMOVE_ROUTE_LIST");
            removeLocalToken();
            resetRouter();
            resolve();
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
    getInfo({ commit }, user) {
      return new Promise((resolve, reject) => {
        console.log("user.account is" + user.account)
        UserService.getUserInfo(user.account)
          .then((resp) => {
            console.log("[LOGIN]Begin to get user info.")
            const data = ResponseExtractor.getData(resp);

            const role = data.role;
            const user = data.user;
            console.log("the account is: " + user.id);
            console.log("the role is: " + data.role);
            console.log("the username is " + user.name);
            commit("SET_ACCOUNT", user.id);
            commit("SET_ROLE", role);
            commit("SET_USERNAME", user.name);
            console.log("[LOGIN]Succeed to get user info.")
            resolve();
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
    generatePermittedRouteList({ commit }, roleType) {
      console.log("get in generatePermittedRouteList and roleType is" + roleType);
      return new Promise((resolve) => {
        console.log("get in filteredRouteList")
        const filteredRouteList = filterAsyncRoutes(asyncRoutes, roleType);
        console.log("filteredRouteList is" + filteredRouteList);

        const fullRouteList = [...defaultRoutes, ...filteredRouteList];
        //const fullRouteList = defaultRoutes.concat(filteredRouteList);
        commit("SET_ROUTE_LIST", fullRouteList);
        router.addRoutes(fullRouteList);
          console.log("Finish in generatePermittedRouteList ");
        resolve();
      });
    },
    resetToken() {
      return new Promise((resolve) => {
        removeLocalToken();
        resolve();
      });
    },
  },
};

export default user;
