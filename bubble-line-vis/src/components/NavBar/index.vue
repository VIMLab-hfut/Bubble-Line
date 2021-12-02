<template>
  <el-row id="nav" type="flex" align="middle">
    <el-col :span="7">
      <span style="font-size: 2.3em;color: #6699CC">BubbleLine:</span>
      <span style="padding-left: 20px;font-size: 1.5em;">
        <span style="color: #6699CC">L</span>inear
        <span style="color: #6699CC">S</span>patial
        <span style="color: #6699CC">D</span>ata
        <span style="color: #6699CC">A</span>nalysis
      </span>
    </el-col>
    <el-col :span="3" :offset="1" style="padding-left: 30px">
      <el-autocomplete
          v-model="condition.site"
          popper-class="my-autocomplete"
          :fetch-suggestions="searchLocation"
          prefix-icon="el-icon-search"
          placeholder="Please enter location"
          @select="selectLocation"
          :trigger-on-focus="false"
      >
        <el-button slot="append" @click="skipToLocation"><i class="el-icon-place"
                                                            style="color:#6699CC; font-size: x-large"></i></el-button>
        <template slot-scope="{ item }">
          <div class="name">{{ item.label }}</div>
          <span class="addr">{{ item.x + " " + item.y }}</span>
        </template>
      </el-autocomplete>
    </el-col>
    <el-col :span="1" :offset="1">
      <i class="el-icon-sort" style="color:#cc6666; font-size: x-large" @click="changeLineVisible"></i>
    </el-col>
    <el-col :span="1">
      <i class="el-icon-share" style="color:#66cc7c; font-size: x-large" @click="changeHeatLineVisible"></i>
    </el-col>
    <el-col :span="1">
      <i class="el-icon-link" style="color:#ccb666; font-size: x-large" @click="changeRibbonVisible"></i>
    </el-col>
    <el-col :span="1">
      <i class="el-icon-news" style="color:#cc6666; font-size: x-large" @click="changeMarkerVisible"></i>
    </el-col>
    <el-col :span="1">
      <i class="el-icon-s-help" style="color:#12c4b4; font-size: x-large" @click="changeHouseVisible"></i>
    </el-col>

    <el-col :span="4" :offset="3" style="">
      <div id="button-container">
        <div :class="{mode_button:true, editing: mode === 'normal' }" style="border-right:0.5px solid #6699CC;"
             @click="intoView">
          <span>View Mode</span>
        </div>
        <div :class="{mode_button:true, editing: mode === 'modification'}" style="border-left:0.5px solid #6699CC;"
             @click="intoEditing">
          <span>Edit Mode</span>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import {OpenStreetMapProvider} from 'leaflet-geosearch';
import {mapState} from "vuex";
import * as L from "leaflet";

export default {
  name: "NavBar",
  data() {
    const osp = new OpenStreetMapProvider();
    return {
      condition: {
        site: '',
      },
      selectedLocation: null,
      provider: osp,
      isEditing: false,
    }
  },
  computed: {
    ...mapState({
      mode: (state) => state.map.mode,
      map: (state) => state.map.leafletMap,
    }),
  },
  methods: {
    changeLineVisible() {
      this.$store.commit('view/commitLineVisible');
    },
    changeHeatLineVisible() {
      this.$store.commit('view/commitHeatLineVisible');
    },
    changeRibbonVisible() {
      this.$store.commit('view/commitRibbonVisible');
    },
    changeMarkerVisible() {
      this.$store.commit('view/commitMarkerVisible');
    },
    changeHouseVisible() {
      this.$store.commit('view/commitHouseVisible');
    },
    intoView() {
      this.$store.commit("map/SET_MODE", "normal");
    },
    intoEditing() {
      this.$store.commit("map/SET_MODE", "modification");
      console.log(this.map)
    },
    async searchLocation(queryString, cb) {
      const results = await this.provider.search({query: this.condition.site});
      for (let r of results) {
        r.value = r.label
      }
      cb(results);
    },
    skipToLocation() {
      let latLng = L.latLng(this.selectedLocation.y, this.selectedLocation.x);
      console.log(latLng)
      this.map.panTo(latLng);
    },
    selectLocation(item) {
      this.selectedLocation = item;
    }
  }
}
</script>

<style scoped>
#nav {
  width: 100%;
  height: 80px;
  background: #000000;
  color: #d4d1d1;
  font-family: Arial;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}

.my-autocomplete {

}

.my-autocomplete li {
  line-height: normal;
  padding: 7px;
}

.my-autocomplete .name {
  text-overflow: ellipsis;
  overflow: hidden;
}

.my-autocomplete .addr {
  font-size: 12px;
  color: #b4b4b4;
  border: #6699CC 1px solid;
}

.my-autocomplete .highlighted .addr {
  color: #ddd;
}

.mode_button {
  width: 100px;
  height: 40px;
  padding-top: 20px;
  color: black;
}

#button-container {
  border: 2px solid #6699CC;
  border-radius: 25px;
  width: 200px;
  height: 40px;
  background: white;
  overflow: hidden;
  display: flex;
  /*实现垂直居中*/
  align-items: center;
  /*实现水平居中*/
  justify-content: center;
}

#button-container:hover {
  cursor: pointer;
}

.editing {
  background: #6699CC;
  color: white;
}
</style>
