import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'
import Login from './views/Login'
import Create from './views/Create'
import Show from './views/Show'
import Refuse from './views/Refuse'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      component: Login
    },
    {
      path: '/home',
      component: Home
    },
    {
      path: '/create/:sid',
      component: Create,
      props: true
    },
    {
      path: '/show/:sid',
      component: Show,
      props: true
    },
    {
      path: '/refuse',
      component: Refuse
    },
  ]
})
