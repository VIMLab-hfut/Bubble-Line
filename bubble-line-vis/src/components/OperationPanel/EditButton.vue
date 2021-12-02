<template>
<div>
  <button class="btn-1" v-if="mode === 'normal'" @click="start">开始编辑</button>
  <button class="btn-2" v-if="mode === 'modification'" @click="end">退出编辑</button>
</div>
  
</template>

<script>
import { mapState } from "vuex";
export default {
  name: "EditButton",
  computed: {
    ...mapState({
      mode: (state) => state.map.mode,}),
  },
  methods: {
    start() {

    },
    end() {

    }
  },
};
</script>

<style scoped>
button {
  padding: 10px 40px;
  font-size: 1rem;
  cursor: pointer;
  border: 0px;
  background: transparent;
  position: relative;
  margin: 20px;
  transition: all 0.25s ease;
}

.btn-2:hover::after {
        transform: translate(-5px, -5px);
    }
    .btn-2:hover::before {
        transform: translate(5px, 5px);
    }
    .btn-2::after,.btn-2::before {
        border: 3px solid #000;
        content: '';
        position: absolute;
        border-radius: inherit;
        width: calc(100% - 6px);
        height: calc(100% - 6px);
        left: 0px;
        bottom: 0px;
        z-index: -1;
        transition: all .25s ease;
    }

    .btn-1 {
        background: #000;
        color: #fff;
        border-radius: 30px;
        transition: all .25s ease;
    }
    .btn-1::after,.btn-1::before {
        content: '';
        position: absolute;
        opacity: .3;
        background: #000;
        border-radius: inherit;
        width: 100%;
        height: 100%;
        left: 0px;
        bottom: 0px;
        z-index: -1;
        transition: all .25s ease;
    }
    .btn-1:hover {
        transform: translate(-12px, -12px);
    }
    .btn-1:hover:after {
        transform: translate(6px, 6px);
    }
    .btn-1:hover:before {
        transform: translate(12px, 12px);
    }
</style>
