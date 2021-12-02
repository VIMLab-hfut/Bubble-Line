<template>
  <div id="info">
    <el-form :model="markerInfo" label-width="90px">
      <el-form-item label="名称">
        <el-input v-model="markerInfo.name" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="经度">
        <el-input
            v-model="markerInfo.longitude"
            autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="纬度">
        <el-input
            v-model="markerInfo.latitude"
            autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item label="海拔">
        <el-input
            v-model="markerInfo.altitude"
            autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">提交</el-button>
        <el-button @click="cancel">取消</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
import {addMarker, updateMarker} from "@/api/pipe-network";
import {mapMutations} from "vuex";
import * as L from "leaflet";

export default {
  name: "MarkerInfoEditWindow",
  computed: {
    markerInfo: {
      get: function () {
        return this.$store.state.view.markerInfo
      },
      set: function (value) {
        this.$store.state.view.markerInfo = value
      },
    },
    map() {
      return this.$store.state.map.leafletMap;
    },
    pipeLineList() {
      return this.$store.state.map.pipeLineList
    },
    //轮廓图生成器
    outlineGenerator() {
      return this.$store.state.map.outlineGenerator;
    },
  },
  methods:{
    ...mapMutations({
      setMarkerInfoVisible: 'view/commitMarkerInfoEditVisible',
      setEditingMarker: 'view/commitMarkerInfo',
      changeServerStatus: 'map/SET_SERVER_CHANGED',

    }),
    onSubmit(){
      if (this.markerInfo.name === "") {
        this.$message({
          message: "请填写管线名",
          type: "warning",
        });
      } else {
        if (this.markerInfo.id === null) {
          this.markerInfo.equipmentId = null;
          addMarker(this.markerInfo).then(() => {
            this.changeServerStatus();
          });
        } else {
          updateMarker(this.markerInfo).then(() => {
            this.changeServerStatus();
          });
        }
        this.setMarkerInfoVisible(false);
      }
    },
    cancel() {
      this.setMarkerInfoVisible(false);
      this.outlineGenerator.removeAllChildren(this.outlineGenerator.outlineGroup);
      let i = this.markerInfo.name === "" ? 0 : Number(this.markerInfo.name);
      this.pipeLineList.ribbons[i].push([this.markerInfo.latitude, this.markerInfo.longitude]);
      this.redrawRibbon();
      this.map.removeLayer(this.markerInfo.equipmentId)
    },
    redrawRibbon() {
      for (let i = 0; i < this.pipeLineList.ribbons.length; i++) {
        for (let anchor of this.pipeLineList.ribbons[i]) {
          let latLng = L.latLng(anchor);
          let point = this.map.latLngToContainerPoint(latLng);
          this.outlineGenerator.pushPoint(point);
        }
        this.outlineGenerator.draw(this.pipeLineList.colors[i]);
      }
    }
  },
  mounted() {
    console.log(this.markerInfo)
  }
}
</script>

<style scoped>
#info {
  /* width: 100px;
  height: 100px; */
  z-index: 2000;
  background: rgb(248, 247, 250);
  /*width: 300px;*/
  /*height: 500px;*/
  padding-top: 15px;
  padding-right: 20px;
  top: calc(25vh);
  left: calc(35vw);
  position: absolute;
  border: 2px solid #d4d1d1;
}
</style>
