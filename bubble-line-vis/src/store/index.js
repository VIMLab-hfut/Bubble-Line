import Vue from 'vue'
import Vuex from 'vuex'
import map from './module/map'
import view from './module/view'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    map,view
  }
})
