<template>
  <div id="app">
    <!--    <router-view/>-->
    <nav-bar></nav-bar>
    <l-map id="l-map"></l-map>
    <sketch-mask></sketch-mask>
  </div>
</template>

<style>

* {
	margin:0;
	padding: 0;
}
#app {
  font-family: "Times New Roman", Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#l-map {
  width: 100%;
  position: absolute;
  top: 80px;
  bottom: 0;
  left: 0;
}
.mx-datepicker-popup {
  /*大于el-drawer的2000，但小于最大值9999*/
  z-index: 2999 !important;
}

</style>
<script>
import LMap from "@/components/Map/index";
import NavBar from "@/components/NavBar";
import SketchMask from "@/views/SketchMask";

export default {
  components: {SketchMask, NavBar, LMap}
}
</script>
