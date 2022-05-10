import Vue from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import store from './store'
import './config/interceptor'


Vue.config.productionTip = false

/* 自定义全局消息组件 */
import GlobalMessage from "./components/GlobalMsgbar/api";
Vue.prototype.$message = GlobalMessage;

/* Ant-Design文件上传组件 */
import { Upload, Icon } from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
Vue.use(Upload);
Vue.use(Icon);
/* 加入Antd的按钮组件*/
import Button from 'ant-design-vue/lib/button';
Vue.use(Button);
/*引入antd的Space */
import Space from 'ant-design-vue/lib/space';
import Progress from 'ant-design-vue/lib/progress';
import Table from 'ant-design-vue/lib/table';
Vue.use(Space);
Vue.use(Progress);
Vue.use(Table);


// 分割面板组件
import splitPane from 'vue-splitpane'
Vue.component('split-pane', splitPane);



new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app')
