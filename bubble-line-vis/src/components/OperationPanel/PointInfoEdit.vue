<template>
  <div id="info">
    <el-form :model="pointInfo" label-width="90px">
      <el-form-item label="Value">
        <el-input v-model="pointInfo.value" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="Weight">
        <el-input
            v-model="pointInfo.weight"
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
import {updatePipePoint} from "@/api/pipe-network";

export default {
  name: "PointInfoEdit",
  props: ['pointInfo'],
  methods: {
    onSubmit() {
      let pipeId = this.pointInfo.pipeId, index = this.pointInfo.index;
      let weight = this.pointInfo.weight;
      let value = this.pointInfo.value;
      updatePipePoint({pipeId, index, value, weight}).then((res) => {
        console.log(res)
      })
      this.pointInfo.visible = false;
      location.reload();
    },
    cancel() {
      this.pointInfo.visible = false;
    }
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
