import Vue from 'vue'
import './plugins/vuetify'
import App from './App.vue'
import router from './router'
import store from './store'
import VueResource from 'vue-resource'

Vue.config.productionTip = false
Vue.use(VueResource)
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
