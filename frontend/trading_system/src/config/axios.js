import axios from "axios";
import GlobalMessage from "../components/GlobalMsgbar/api";
import ResponseExtractor from "../utils/response-extractor";
import store from "../store"

const HttpService = axios.create({
  baseURL: 'http://127.0.0.1:16666',
  timeout: 30000
})


HttpService.interceptors.request.use(function (config) {
  if (localStorage.token) {
    config.headers.token = localStorage.getItem('token')
  }
  return config;
}, function (error) {
  console.log('request-error: ' + error); //for debug
  return Promise.reject(error);
});


HttpService.interceptors.response.use(
  resp => {
    // 根据服务端自定义错误码识别自定义异常，弹出提示，中断响应
    const success = ResponseExtractor.isSuccessful(resp);
    if (!success) {
      const code = ResponseExtractor.getCode(resp);
      /**
       * 此处是为了处理本地存在token但为无效token的情况（包括被篡改和过期）
       *  该情况下需要跳到登录页让用户重新登录，但路由拦截器检测到有token就不会跳转到登录页
       * 所以针对后台响应作特殊处理，如果发现token无效，直接清空本地token然后刷新页面
       * 剩下的交给导航守卫
       */
      if (code === 401 || code === 403) {
        GlobalMessage.error('登录已过期，请重新登录');
        store.dispatch('user/resetToken').then(() => {
          location.reload();
        })
      } else {
        GlobalMessage.error(resp.data.msg);
        return Promise.reject(new Error(resp.data.msg || 'Error'));
      }
    } else {
      return resp;
    }
  },
  error => {
    console.log('response-error: ' + error); //for debug
    return Promise.reject(error);
  }
);

export default HttpService;