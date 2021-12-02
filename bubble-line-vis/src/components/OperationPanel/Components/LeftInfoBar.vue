<template>
  <div style="position: absolute; top: 6px;left: 7px">
    <div class="nav">
      <div class="container1">
        <div :class="{mode_button:true, checked: column1 }" @click="column1 = !column1">
          <span>在售房源</span>
        </div>
        <div :class="{mode_button:true, checked: !column1}" @click="column1 = !column1">
          <span>最近成交</span>
        </div>
      </div>
    </div>
    <div class="content">
      <div style="margin: 0 auto;width: 370px;height: 807px;overflow: scroll;">
        <div class="content-card" v-for="i in 10">
          <el-row>
            <el-col :span="12">
              <p style="text-align:left;margin-top: 11px; margin-left: 10px">
                单价： <span style="color: #b5c9f6">{{ houseInfos[i - 1].unitPrice }}</span>元/平<br>
                总面积： <span style="color: #b5c9f6">{{ houseInfos[i - 1].area }}</span>平<br>
                挂牌： <span style="color: #b5c9f6">{{ houseInfos[i - 1].date }}</span><br>
                装修： <span style="color: #b5c9f6">{{ houseInfos[i - 1].type }}</span><br>
                小区： <span style="color: #b5c9f6">{{ houseInfos[i - 1].estateName }}</span><br>
                楼层： <span style="color: #b5c9f6">{{ houseInfos[i - 1].floor }}</span><br>
                年代： <span style="color: #b5c9f6">{{ houseInfos[i - 1].buildTime }}</span>
              </p>
            </el-col>
            <el-col :span="12">
              <div :id="'echartsLine'+i" style="width: 170px;height:170px;"></div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";

export default {
  name: "LeftInfoBar",
  data() {
    return {
      column1: true,
      houseInfos: [
        {
          unitPrice: 24464,
          area: 223,
          date: "2021.10.27",
          type: "精装",
          estateName: "万达公馆",
          floor: "低楼层（共47层）",
          buildTime: "2012年建成",
          purpose: "普通住宅",
          averagePrice: [25454, 23564, 19892]
        },
        {
          unitPrice: 21820,
          area: 143,
          date: "2021.10.17",
          type: "毛坯",
          estateName: "坝上街环球中心",
          floor: "低楼层",
          buildTime: "2014年建成",
          purpose: "普通住宅",
          averagePrice: [22903, 23090, 19892]
        },
        {
          unitPrice: 16201,
          area: 124,
          date: "2021.10.12",
          type: "精装",
          estateName: "锦绣豪庭",
          floor: "低楼层（共32层）",
          buildTime: "2012年建成",
          purpose: "普通住宅",
          averagePrice: [15245, 18054, 19892]
        },
        {
          unitPrice: 25235,
          area: 223,
          date: "2021.10.23",
          type: "简装",
          estateName: "万达公馆",
          floor: "低楼层（共47层）",
          buildTime: "2012年建成",
          purpose: "普通住宅",
          averagePrice: [25454, 23564, 19892]
        },
        {
          unitPrice: 24464,
          area: 223,
          date: "2021.10.27",
          type: "精装",
          estateName: "万达公馆",
          floor: "低楼层（共47层）",
          buildTime: "2012年建成",
          purpose: "普通住宅",
          averagePrice: [25454, 23564, 19892]
        },
        {
          unitPrice: 21820,
          area: 143,
          date: "2021.10.17",
          type: "毛坯",
          estateName: "坝上街环球中心",
          floor: "低楼层",
          buildTime: "2014年建成",
          purpose: "普通住宅",
          averagePrice: [22903, 23090, 19892]
        },
        {
          unitPrice: 16201,
          area: 124,
          date: "2021.10.12",
          type: "精装",
          estateName: "锦绣豪庭",
          floor: "低楼层（共32层）",
          buildTime: "2012年建成",
          purpose: "普通住宅",
          averagePrice: [15245, 18054, 19892]
        },
        {
          unitPrice: 25235,
          area: 223,
          date: "2021.10.23",
          type: "简装",
          estateName: "万达公馆",
          floor: "低楼层（共47层）",
          buildTime: "2012年建成",
          purpose: "普通住宅",
          averagePrice: [25454, 23564, 19892]
        }, {
          unitPrice: 24464,
          area: 223,
          date: "2021.10.27",
          type: "精装",
          estateName: "万达公馆",
          floor: "低楼层（共47层）",
          buildTime: "2012年建成",
          purpose: "普通住宅",
          averagePrice: [25454, 23564, 19892]
        },
        {
          unitPrice: 21820,
          area: 143,
          date: "2021.10.17",
          type: "毛坯",
          estateName: "坝上街环球中心",
          floor: "低楼层",
          buildTime: "2014年建成",
          purpose: "普通住宅",
          averagePrice: [22903, 23090, 19892]
        }
      ]
    }
  },
  mounted() {
    for (let i = 0; i < 10; i++) {
      let myChart = echarts.init(document.getElementById('echartsLine' + (i + 1)));
      myChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {},
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          boundaryGap: [0, 0.01],
          axisLabel: {
            show: false
          }
        },
        yAxis: {
          type: 'category',
          data: ['同小区', '周边', '同地区']
        },
        series: [
          {
            name: '实际',
            type: 'bar',
            data: [this.houseInfos[i].unitPrice, this.houseInfos[i].unitPrice, this.houseInfos[i].unitPrice]
          },
          {
            name: '平均',
            type: 'bar',
            data: this.houseInfos[i].averagePrice
          }
        ]
      });
    }

  }
}
</script>

<style scoped>
.container1 {
  width: 314px;
  border-bottom: 2px solid #c0c4cc;
  left: 30px;
  top: 10px;
  position: absolute;
}

.nav {
  height: 60px;
  width: 384px;
  top: 5px;
  background: rgba(0, 0, 0, 0.6);
}

.mode_button {
  width: 100px;
  height: 27px;
  color: #6699CC;
  display: inline-block;
  padding-top: 10px;
  margin-bottom: 5px;
}

.checked {
  border-top: 1px solid #c0c4cc;
  border-right: 1px solid #c0c4cc;
  border-left: 1px solid #c0c4cc;
  border-top-right-radius: 5px;
  border-top-left-radius: 5px;
}

.content {
  width: 384px;
  height: 300px;
}

.content-card {
  width: 330px;
  margin-top: 10px;
  margin-bottom: 40px;
  height: 180px;
  background: #ffffff;
  border: 2px solid #6699CC;
  border-radius: 10px 30px 10px 30px;
}

/*定义滚动条高宽及背景 高宽分别对应横竖滚动条的尺寸*/
::-webkit-scrollbar {
  width: 9px;
  height: 16px;
  background-color: rgba(0, 0, 0, 0.2);
}

/*定义滚动条轨道 内阴影+圆角*/
::-webkit-scrollbar-track {
  -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
  border-radius: 10px;
  background-color: rgba(0, 0, 0, 0.2);
}

/*定义滑块 内阴影+圆角*/
::-webkit-scrollbar-thumb {
  border-radius: 10px;
  -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, .3);
  background-color: #6699CC;
}
</style>
