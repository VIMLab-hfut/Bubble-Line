
const state = {
    pipeLineInfoEditVisible: false,
    pipeLineInfo: null,
    markerInfoEditVisible: false,
    markerInfo: null,
    lineVisible: false,
    ribbonVisible: false,
    heatLineVisible: false,
    markerVisible: false,
    heatLineLayer: null,
    editingLineLayer: null,
    houseVisible: false
}

const mutations = {
    commitPipeLineInfoEditVisible: (state, visible) => {
        state.pipeLineInfoEditVisible = visible;
    },
    commitPipeLineInfo: (state, info) => {
        state.pipeLineInfo = info;
    },
    commitMarkerInfoEditVisible: (state) => {
        state.markerInfoEditVisible = !state.markerInfoEditVisible;
    },
    commitMarkerInfo: (state, info) => {
        state.markerInfo = info;
    },
    commitLineVisible: (state) => {
        state.lineVisible = !state.lineVisible;
    },
    commitRibbonVisible: (state) => {
        state.ribbonVisible = !state.ribbonVisible;
    },
    commitHeatLineVisible: (state) => {
        state.heatLineVisible = !state.heatLineVisible;
    },
    commitMarkerVisible: (state) => {
        state.markerVisible = !state.markerVisible;
    },
    commitHeatLineLayer: (state, layer) => {
        state.heatLineLayer = layer
    },
    commitEditingLineLayer: (state, layer) => {
        state.editingLineLayer = layer
    },
    commitHouseVisible: (state) => {
        state.houseVisible = !state.houseVisible
    }
}

const actions = {

}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
