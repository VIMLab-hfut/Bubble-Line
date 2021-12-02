import Vue from 'vue'
import VueRouter from 'vue-router'
import SketchMask from '../views/SketchMask.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: SketchMask
  },
  // {
  //   path: '/test',
  //   name: 'Test',
  //   component: () => import('@/views/Test')
  // }
]

const router = new VueRouter({
  routes
})

export default router
