<template>
  <div id="myTestChart" style="width: 600px; height: 400px"></div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "Test",
  data() {
    return {
      chartOption: {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // Use axis to trigger tooltip
            type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
          }
        },
        legend: {
          top: '7%',
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: ['Q7', 'Q6', 'Q5', 'Q4', 'Q3', 'Q2', 'Q1']
        },
        series: [
          {
            name: '5',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            data: [15, 11, 15, 8, 18, 14, 10]
          },
          {
            name: '4',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            data: [3, 7, 3, 5, 3, 3, 6]
          },
          {
            name: '3',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            data: [2, 3, 3, 6, 2, 3, 2]
          },
          {
            name: '2',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            data: [2, 2, 2, 1, 0, 2, 2]
          },
          {
            name: '1',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            data: [1, 0, 0, 3, 0, 1, 3]
          }
        ]
      }
    }
  },
  mounted() {
    let myChart = echarts.init(document.getElementById('myTestChart'));
    myChart.setOption(this.chartOption);
  }
}
</script>

<style scoped>

</style>
