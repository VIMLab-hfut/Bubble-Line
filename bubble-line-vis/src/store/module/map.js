import {getList} from '@/api/pipe-network'


const state = {
  //系统当前模式,normal正常模式，modification为编辑模式
  mode: 'normal',
  //后台数据是否修改
  serverChanged: false,
  //用于保存后台管线信息列表
  pipeLineList: [],
  infoVisible: false,
  focusHeatLineNodes: [],
  leafletMap: null,
  outlineGenerator: null,
  ribbonNodes: null
}

const mutations = {
  SET_MODE: (state,mode) => {
    state.mode = mode;
  },
  SET_SERVER_CHANGED: (state) => {
    state.serverChanged = !state.serverChanged;
  },
  SET_PIPE_LIST: (state,list) => {
    state.pipeLineList = list;
  },
  SET_INFO_VISIBLE: (state, visible) => {
    state.infoVisible = visible;
  },
  SET_LEAFLET_MAP: (state, map) => {
    state.leafletMap = map;
  },
  SET_OUTLINE_GENERATOR: (state, object) => {
    state.outlineGenerator = object;
  },
  SET_RIBBON_NODES: (state, object) => {
    state.ribbonNodes = object;
  },
}

const actions = {
  changeMode({ commit }, mode) {
    commit('SET_MODE', mode)
  },
  changePipeList({ commit }, page) {
    return new Promise((resolve, reject) =>{
      getList(page).then(response => {
        commit('SET_PIPE_LIST', response.data)
        resolve()
      }).catch(error => {
        reject(error)
      })
    });
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
