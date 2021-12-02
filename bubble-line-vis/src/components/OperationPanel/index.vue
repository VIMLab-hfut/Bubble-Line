<template>
  <section>
    <aside>
      <label>
        <span>线宽增减： </span>
        -7 <input id="weight" type="range" min="-7" max="8" value="5" v-model="changedWeight" @change="changeWeight"/> 8
      </label>
      <br />

      <label>
        <span>Min - </span>
        100
        <input
            id="min"
            type="range"
            min="100"
            max="250"
            value="150"
            steps="5"
            v-model="min"
            @change="changeMinAndMax('min')"
        />
        250
      </label>
      <label>
        <span>Max - </span>
        250
        <input
            id="max"
            type="range"
            min="250"
            max="500"
            value="350"
            steps="5"
            v-model="max"
            @change="changeMinAndMax('max')"
        />
        500
      </label>
      <br/>
      <label>
        <span>0%</span>
        <input id="paletteColor1" type="color" v-model="color1" @change="changeColor"/>
      </label>
      <label>
        <span>50%</span>
        <input id="paletteColor2" type="color" v-model="color2" @change="changeColor"/>
      </label>
      <label>
        <span>100%</span>
        <input id="paletteColor3" type="color" v-model="color3" @change="changeColor"/>
      </label>
      <br/>
      <label>
        <span>轮廓能量场： </span>
        20 <input type="range" min="20" max="500" value="5" v-model="changedOutlineWidth"
                  @change="changeOutlineWidth"/> 500
      </label>
      <el-date-picker
          v-model="time"
          type="monthrange"
          range-separator="至"
          start-placeholder="开始月份"
          end-placeholder="结束月份">
      </el-date-picker>

    </aside>


    <el-collapse accordion>
      <el-collapse-item v-for="(item, i) in pipeLineList.pipelines">

        <template slot="title">
          <span style="width: 10px"></span>
          {{ item.name }}
          <span style="width: 10px"></span>
          <div style="width: 100px; height:2px;display: inline-block;" :style=" {background: item.lineColor}"></div>
        </template>
        描述信息
      </el-collapse-item>
    </el-collapse>
    <div id="echartsLine" style="width: 600px;height:350px;"></div>
  </section>
</template>

<script>
import * as L from "leaflet";
import {option} from "./Components/options";
import * as echarts from 'echarts';

export default {
  name: "index",
  data() {
    return {
      changedWeight: 0,
      color1: "#008800",
      color2: "#ffff00",
      color3: "#ff0000",
      min: 0,
      max: 250,
      time: [new Date(2021, 9, 10, 8, 40), new Date(2021, 9, 10, 9, 40)],
      changedOutlineWidth: 20
    }
  },
  computed: {
    //所有的heatLine
    heatLineLayer() {
      return this.$store.state.view.heatLineLayer
    },
    //当前点击的heatLine
    editingLine() {
      return this.$store.state.view.editingLineLayer
    },
    //轮廓图生成器
    outlineGenerator() {
      return this.$store.state.map.outlineGenerator;
    },
    ribbons() {
      return this.$store.state.map.ribbonNodes;
    },
    map() {
      return this.$store.state.map.leafletMap;
    },
    pipeLineList() {
      return this.$store.state.map.pipeLineList
    },
    pipes() {
      return this.$store.state.map.pipeLineList.pipelines
    }
  },
  methods: {
    changeWeight() {
      let arr = this.editingLine.options.extraValue;
      let temp = arr.forEach((item, index, arr) => {
        arr[index] += Number(this.changedWeight);
      })
      this.editingLine.redraw()
    },
    changeColor() {
      this.heatLineLayer.setStyle({
        'palette': {
          0.0: this.color1,
          0.5: this.color2,
          1.0: this.color3
        }
      });
    },
    changeMinAndMax(key) {
      if (key === 'max') {
        this.heatLineLayer.setStyle({max: Number(this.max)});
      } else if (key === 'min') {
        this.heatLineLayer.setStyle({min: Number(this.min)});
      }
    },
    changeOutlineWidth() {
      this.outlineGenerator.removeAllChildren(this.outlineGenerator.outlineGroup);
      let generator = this.outlineGenerator.pathGenerator;
      generator.outlineWidth(this.changedOutlineWidth);
      this.redrawRibbon();
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
    let myChart = echarts.init(document.getElementById('echartsLine'));
    myChart.setOption(option);
  }
}
</script>

<style scoped>
section {
  max-width: 600px;
  /* margin: 1.5em auto; */
  text-align: center;
}

aside {
  margin: 1.5em 0;
}

label {
  display: inline-block;
  padding: 0.5em;
}

input {
  vertical-align: text-bottom;
}

</style>
