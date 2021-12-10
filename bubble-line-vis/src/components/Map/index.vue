<template>
  <div>
    <div id="map"></div>
    <point-info-edit v-if="pointInfo.visible" :pointInfo=pointInfo></point-info-edit>
  </div>
</template>

<script>
import "leaflet/dist/leaflet.css";
import * as L from "leaflet";
import "leaflet.chinatmsproviders";
import "@geoman-io/leaflet-geoman-free";
import "@geoman-io/leaflet-geoman-free/dist/leaflet-geoman.css";


import {mapMutations, mapState} from "vuex";
import "./components/heat-line.js";
import HeatmapOverlay from "./components/leaflet-heatmap.js";
import {BubbleLine} from "./components/bubble-line";

import {deleteMarker, deletePipe, getList, getMarkerList, getPipe, updateMarker, updatePipe} from "@/api/pipe-network";

import PointInfoEdit from "../OperationPanel/PointInfoEdit";
import {getPopulationList} from "../../api/leaflet-map";


export default {
  name: "LMap",
  components: {PointInfoEdit},
  data() {
    return {
      map: null,
      //编辑模式下，保存当前选中的管线信息
      formTemp: null,
      //编辑模式下，保存当前选中的marker信息
      editingMarker: {},
      //用来判断当前事件是否是点击事件，防止触发点击事件
      notClickEvent: false,
      //黄山
      //centerP: {lng: 118.3092373345947, lat: 29.697865719744868},
      //合肥
      centerP: {lng: 117.26296471923827, lat: 31.832022434850202},
      markers: [],
      groupedPipeline: [],
      updatedLayers: new Set(),

      //分组管理图层
      lineGroup: L.featureGroup(),
      markerGroup: L.featureGroup(),
      heatLineGroup: L.featureGroup(),
      ribbonGroup: L.featureGroup(),
      heatmapLayer: null,

      //点数据信息
      pointInfo: {
        pipeId: 0,
        index: -1,
        value: 0.0,
        weight: 0.0,
        visible: false
      },
      populationList: null,
      houseList: null,
      popUpData: {
        a: 5231,
        b: 23564,
        c: 79,
        num: 0,
      }
    };
  },

  computed: {
    ...mapState({
      mode: (state) => state.map.mode,
      infoVisible: (state) => state.map.infoVisible,
      serverChanged: (state) => state.map.serverChanged,
      lineVisible: (state) => state.view.lineVisible,
      ribbonVisible: (state) => state.view.ribbonVisible,
      heatLineVisible: (state) => state.view.heatLineVisible,
      markerVisible: (state) => state.view.markerVisible,
      houseVisible: (state) => state.view.houseVisible
    }),
    pipes: function () {
      return this.groupedPipeline.pipelines;
    },
    heatLines: function (){
      return this.groupedPipeline.heatLines;
    },
    ribbons: function () {
      return this.groupedPipeline.ribbons;
    }
  },
  watch: {
    //watch里不能使用箭头函数
    mode: function (newV, oldV) {
      if (newV === "modification") {
        this.initGeoman(this.map);
      } else {
        this.map.pm.removeControls();
      }
    },
    markerVisible: function (newV, oldV) {
      if (newV) {
        this.drawMarker();
      } else {
        this.markerGroup.clearLayers();
      }
    },
    lineVisible: function (newV, oldV) {
      if (newV) {
        this.drawLine();
      } else {
        this.lineGroup.clearLayers();
      }
    },
    heatLineVisible: function (newV, oldV) {
      if (newV) {
        this.drawHeatLine();
      } else {
        this.heatLineGroup.clearLayers();
      }
    },
    ribbonVisible: function (newV, oldV) {
      if (newV) {
        // let heatLine = document.querySelector(".leaflet-overlay-pane canvas");
        // if (heatLine != null) {
        //   heatLine.style.zIndex = 800;
        // }

        this.drawRibbon();
      } else {
        // let heatLine = document.querySelector(".leaflet-overlay-pane canvas");
        // if (heatLine != null) {
        //   heatLine.style.zIndex = 100;
        // }
        this.ribbonGroup.clearLayers();
      }
    },
    houseVisible: function (newV, oldV) {
      if (newV) {
        this.drawHeatmap();
        // getScatters().then((res) => {
        //   for (let point of res.data) {
        //     L.circle([point.latitude, point.longitude], {
        //       color: point.color,
        //       fillColor: point.color,
        //       fillOpacity: 0.8,
        //       radius: 50,
        //     }).addTo(this.map);
        //   }
        // })
      } else {
        this.map.removeLayer(this.heatmapLayer);
      }
    },
    serverChanged: function (newV, oldV) {
      console.log(
          "%c[SUCCESS]",
          "color: white; background: green;",
          " 服务端状态改变了"
      );
      this.flushData();
      //TODO 是否清空
    },
  },
  methods: {
    ...mapMutations({
      // 将 `this.setPipelineInfoVisible()` 映射为 `this.$store.commit('view/commitPipeLineInfoEditVisible')`
      setPipelineInfoVisible: 'view/commitPipeLineInfoEditVisible',
      setEditingPipeline: 'view/commitPipeLineInfo',
      setMarkerInfoVisible: 'view/commitMarkerInfoEditVisible',
      setEditingMarker: 'view/commitMarkerInfo',
      changeServerStatus: 'map/SET_SERVER_CHANGED',
      setHeatLineLayer: 'view/commitHeatLineLayer',
      setEditingLine: 'view/commitEditingLineLayer',
      setOutlineGenerator: 'map/SET_OUTLINE_GENERATOR',
      setRibbonNodes: 'map/SET_RIBBON_NODES',
      setPipeList: 'map/SET_PIPE_LIST',
    }),
    async flushData() {
      this.lineGroup.clearLayers();
      this.heatLineGroup.clearLayers();
      this.ribbonGroup.clearLayers();
      this.markerGroup.clearLayers();

      await getMarkerList(null).then((res) => (this.markers = res.data));
      await getList(null).then((res) => {
        this.groupedPipeline = res.data;
        this.setPipeList(res.data);
        console.log(res.data)
      });

      if (this.lineVisible) {
        this.drawLine();
      }
      if (this.heatLineVisible) {
        this.drawHeatLine();
      }
      if (this.ribbonVisible) {
        this.drawRibbon();
      }
      if (this.markerVisible) this.drawMarker();
    },
    drawLine() {
      //绘制管线
      for (let i in this.pipes) {
        let line;
        // for (const latLng of this.pipes[i].nodes) {
        //   L.circle(latLng, {
        //     color: "red",
        //     fillColor: "yellow",
        //     fillOpacity: 0.8,
        //     radius: 6,
        //   }).addTo(this.map);
        // }
        try {
          line = L.polyline(this.pipes[i].nodes, {
            color: this.pipes[i].lineColor,
            weight: this.pipes[i].lineWeight,
          }).addTo(this.lineGroup);
        } catch (e) {
          console.log(e);
          console.log(
              "%c[Failed]",
              "color: white; background: red;",
              this.pipes[i].name + "管线数据有误"
          );
          continue;
        }

        line.bindTooltip(this.pipes[i].name + "<br>周边人口密度：5231 人/平方公里<br> 周边房价均价：23564元/平方米<br>在售房源：79套", {
          interactive: true,
          sticky: true,
          className: 'leaflet-label' //自定义css控制
        });
        line.id = this.pipes[i].id;
        line.isPipe = true;
      }
    },
    drawMarker () {
      let myIcon = this.changeGeomanDefaultIcon(this.map);
      //绘制marker
      for (let i in this.markers) {
        let marker = L.marker(
            [this.markers[i].latitude, this.markers[i].longitude],
            {icon: myIcon}
        )
            .addTo(this.markerGroup)
            .bindPopup(
                '<span style="color:#006699; ">' + this.markers[i].name + "</span>",
                {className: "mypopup"}
            );
        marker.id = this.markers[i].id;
        marker.isPipe = false;
      }
    },
    drawHeatLine() {
      let i = 0;
      for (let heatLine of this.heatLines) {
        //绘制填充后的离散点
        let k = 0;
        for (const latLng of heatLine.node) {
          /*if (k++ % 4 === 0) {
            L.circle(latLng, {
              color: "blue",
              fillColor: "blue",
              fillOpacity: 0.8,
              radius: 50,
            }).bindPopup((e) => {
              return latLng[2].toString();
            }).addTo(this.map);
          }*/

        }
        let heatLineLayer = L.heatLine(heatLine.node, {
          min: 150,
          max: 350,
          palette: {
            0.0: "#005B5D",
            0.5: "#EBDC00",
            1.0: "#890F15",
          },
          weight: 5,
          outlineColor: "#000000",
          outlineWidth: 0,
          extraValue: heatLine.weight,
        });
        heatLineLayer.id = this.pipes[i++].id;
        heatLineLayer
            // .bindPopup((e) => {
            //   return "HHHHHHH";
            // })
            .addTo(this.heatLineGroup);

      }
      this.setHeatLineLayer(this.heatLineGroup);
      this.heatLineGroup.on('click', (e) => {
        this.setEditingLine(e.layer);
      })
      //自动缩放到最佳比例
      //let bounds = heatLineLayer.getBounds();
      //this.map.fitBounds(bounds);

    },
    drawHeatmap() {
      getPopulationList().then((res) => {
        let houseData = {
          max: 10000,
          min: 1000,
          data: res.data
        }
        let cfg = {
          // radius should be small ONLY if scaleRadius is true (or small radius is intended)
          "radius": 0.008,
          "maxOpacity": .8,
          // scales the radius based on map zoom
          "scaleRadius": true,
          // if set to false the heatmap uses the global maximum for colorization
          // if activated: uses the data maximum within the current map boundaries
          //   (there will always be a red spot with useLocalExtremas true)
          "useLocalExtrema": true,
          // which field name in your data represents the latitude - default "lat"
          latField: 'latitude',
          // which field name in your data represents the longitude - default "lng"
          lngField: 'longitude',
          // which field name in your data represents the data value - default "value"
          valueField: 'density'
        };
        this.heatmapLayer = new HeatmapOverlay(cfg);
        this.heatmapLayer.setData(houseData);
        this.heatmapLayer.addTo(this.map);
      })
    },
    drawRibbon() {
      let mapSize = this.map.getPixelBounds().getSize();
      let riverContour = new BubbleLine(mapSize.x.toString(), mapSize.y.toString());
      this.setOutlineGenerator(riverContour);
      this.setRibbonNodes(this.ribbons);
      for (let i = 0; i < this.ribbons.length; i++) {
        for (let anchor of this.ribbons[i]) {
          let latLng = L.latLng(anchor);
          let point = this.map.latLngToContainerPoint(latLng);
          riverContour.pushPoint(point);
        }
        riverContour.draw(this.groupedPipeline.colors[i]);
      }
      L.svgOverlay(riverContour.svgElement, this.map.getBounds()).addTo(this.ribbonGroup);

      /*for(let latLng of l) {
        L.circle(latLng, {
          color: "red",
          fillColor: "#f03",
          fillOpacity: 0.5,
          radius: 10,
        }).addTo(this.map);
      }
      for(let latLng of l2) {
        L.circle(latLng, {
          color: "red",
          fillColor: "#f03",
          fillOpacity: 0.5,
          radius: 10,
        }).addTo(this.map);
      }*/
    },

    /**
     * 编辑模式下单击线
     */
    updateInEditMode() {
      return (e) => {
        let points = e.layer._latlngs;
        let currentPoint = e.markerEvent.latlng;
        //当前编辑点在所在线组中的下标
        let i = 0;
        for (let key in points) {
          if (currentPoint.lng === points[key].lng && currentPoint.lat === points[key].lat) i = key;
        }

        //{pipeId: points[i].id, i}
        let pipeId = e.layer.id, index = i;
        getPipe({pipeId, index}).then((res) => {
          this.pointInfo.index = index;
          this.pointInfo.pipeId = pipeId;
          this.pointInfo.weight = res.data.weight;
          this.pointInfo.value = res.data.value;
          this.pointInfo.visible = true;
        });
        console.log(e)
      }
    },
    /**
     * 使用闭包避免获取不到Vue组件的上下文
     */
    batchUpdate() {
      return (e) => {
        console.log("触发了更新")
        //geoman进入edit模式后
        if (e.enabled === true) {
          this.notClickEvent = true;
          this.updatedLayers = new Set();
        } else {
          //退出edit模式或进入其他模式
          this.notClickEvent = false;
          for (let key of this.updatedLayers.keys()) {
            let updated = this.lineGroup._layers[key];
            if (updated.isPipe === true) {
              let pipe = this.findPipe(updated.id);
              pipe.nodes = [];

              for (let i in updated._latlngs) {
                //转换为后台所需格式
                pipe.nodes.push([
                  updated._latlngs[i].lat,
                  updated._latlngs[i].lng,
                ]);
              }
              updatePipe(pipe).then(() => {
                this.changeServerStatus();
              });
            } else {
              let marker = this.findMarker(updated.id);
              console.log(updated);
              marker.longitude = updated._latlng.lng;
              marker.latitude = updated._latlng.lat;
              updateMarker(marker).then(() => {
                this.changeServerStatus();
              });
            }
          }
        }
      };
    },
    onLayerRemove() {
      return (e) => {
        this.notClickEvent = true;
        this.$confirm("此操作将永久删除, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            if (e.shape === "Marker") {
              deleteMarker(e.layer.id);
            }
            if (e.shape === "Line") {
              deletePipe(e.layer.id);
            }
            this.$message({
              type: "success",
              message: "删除成功!",
            });
          })
          .catch(() => {
            this.$message({
              type: "info",
              message: "已取消删除",
            });
            this.flushData();
          });
        this.notClickEvent = false;
      };
    },
    onLayerCreated() {
      return (e) => {
        if (e.shape === "Marker") {
          this.editingMarker = {
            id: null,
            name: "",
            latitude: e.marker._latlng.lat,
            longitude: e.marker._latlng.lng,
            altitude: null,
            equipmentId: e.layer,
          };
          this.setEditingMarker(this.editingMarker);
          this.setMarkerInfoVisible();
        }

        if (e.shape === "Line") {
          //添加线后，打开信息编辑窗口，保存入库
          this.formTemp = {
            id: null,
            flow: 1000.0,
            frictionCoefficient: 0.1,
            hydraulicPressure: 0.28,
            lineColor: "#993333",
            groupNumber: 1,
            lineWeight: 2,
            diameter: 300,
            direction: 1,
            length: null,
            manufacturer: "",
            name: "",
            nodes: null,
            texture: "",
            initialWeights: [],
            initialValues: []
          };
          this.setEditingPipeline(this.formTemp);
          //将捕捉到的layer里的经纬度数组转换为对应格式
          let arr = [];
          for (let i = 0; i < e.layer._latlngs.length; i++) {
            arr.push([e.layer._latlngs[i].lat, e.layer._latlngs[i].lng]);
          }
          this.formTemp.nodes = arr;
          // console.log(e);
          this.setPipelineInfoVisible(true);
        }
      };
    },

    findPipe(id) {
      for (let i in this.pipes) {
        if (this.pipes[i].id == id) {
          return this.pipes[i];
        }
      }
    },
    findMarker(id) {
      for (let i in this.markers) {
        if (this.markers[i].id == id) {
          return this.markers[i];
        }
      }
    },
    exitDrawMode() {
      return (e) => {
        if (e.enabled === false && e.shape === "Marker") {
          this.flushData();
        }
      };
    },
    /**
     * 注：Geoman插件默认的Marker图标加载不了，需要修改
     * 本方法更改默认的Icon
     */
    changeGeomanDefaultIcon(map) {
      let markerIcon = require("@/assets/image/合肥地铁.png");
      let markerIcon2x = require("@/assets/image/合肥地铁-2x.png");
      let markerShadow = require("@/assets/image/marker-shadow.png");
      let DefaultIcon = L.icon({
        iconUrl: markerIcon,
        iconRetinaUrl: markerIcon2x,
        shadowUrl: markerShadow,
        iconSize: [23, 24],
        iconAnchor: [11, 24],
        popupAnchor: [2, -28],
        tooltipAnchor: [0, -24],
        shadowSize: [30, 30],
        shadowAnchor: [8, 30],
      });
      //改变leaflet默认图标
      L.Marker.prototype.options.icon = DefaultIcon;

      let markerStyle = { icon: DefaultIcon };
      //改变geoman默认图标
      map.pm.setGlobalOptions({ markerStyle: markerStyle });
      return DefaultIcon;
    },
    /**
     * 注：2019年1月1日起天地图API及服务接口调用都需要获得开发授权,需要申请Key
     * 本方法更改默认的key
     */
    changeTianDiTuKey() {
      L.TileLayer.ChinaProvider = L.TileLayer.extend({
        initialize: function (type, options) {
          // (type, Object)
          var providers = L.TileLayer.ChinaProvider.providers;

          var parts = type.split(".");

          var providerName = parts[0];
          var mapName = parts[1];
          var mapType = parts[2];

          var url = providers[providerName][mapName][mapType];
          options.subdomains = providers[providerName].Subdomains;

          L.TileLayer.prototype.initialize.call(this, url, options);
        },
      });
      L.TileLayer.ChinaProvider.providers = {
        TianDiTu: {
          Normal: {
            Map: "https://t{s}.tianditu.gov.cn/vec_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=8ac4041d5674349dab32efedfd36082a",
            Annotion:
              "https://t{s}.tianditu.gov.cn/cva_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cva&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=8ac4041d5674349dab32efedfd36082a",
          },
          Satellite: {
            Map: "https://t{s}.tianditu.gov.cn/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=8ac4041d5674349dab32efedfd36082a",
            Annotion:
                "https://t{s}.tianditu.gov.cn/cia_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cia&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=8ac4041d5674349dab32efedfd36082a",
          },
          Terrain: {
            Map: "https://t{s}.tianditu.gov.cn/ter_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=ter&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=8ac4041d5674349dab32efedfd36082a",
            Annotion:
                "https://t{s}.tianditu.gov.cn/cta_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=cta&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=8ac4041d5674349dab32efedfd36082a",
          },
          Subdomains: ["0", "1", "2", "3", "4", "5", "6", "7"],
        },
        GaoDe: {
          Normal: {
            Map: '//webrd0{s}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}'
          },
          Satellite: {
            Map: '//webst0{s}.is.autonavi.com/appmaptile?style=6&x={x}&y={y}&z={z}',
            Annotion: '//webst0{s}.is.autonavi.com/appmaptile?style=8&x={x}&y={y}&z={z}'
          },
          Subdomains: ["1", "2", "3", "4"]
        },

        Google: {
          Normal: {
            Map: "//www.google.cn/maps/vt?lyrs=m@189&gl=cn&x={x}&y={y}&z={z}"
          },
          Satellite: {
            Map: "//www.google.cn/maps/vt?lyrs=s@189&gl=cn&x={x}&y={y}&z={z}",
            Annotion: "//www.google.cn/maps/vt?lyrs=y@189&gl=cn&x={x}&y={y}&z={z}"
          },
          Subdomains: []
        },

        Geoq: {
          Normal: {
            Map: "//map.geoq.cn/ArcGIS/rest/services/ChinaOnlineCommunity/MapServer/tile/{z}/{y}/{x}",
            PurplishBlue: "//map.geoq.cn/ArcGIS/rest/services/ChinaOnlineStreetPurplishBlue/MapServer/tile/{z}/{y}/{x}",
            Gray: "//map.geoq.cn/ArcGIS/rest/services/ChinaOnlineStreetGray/MapServer/tile/{z}/{y}/{x}",
            Warm: "//map.geoq.cn/ArcGIS/rest/services/ChinaOnlineStreetWarm/MapServer/tile/{z}/{y}/{x}",
          },
          Theme: {
            Hydro: "//thematic.geoq.cn/arcgis/rest/services/ThematicMaps/WorldHydroMap/MapServer/tile/{z}/{y}/{x}"
          },
          Subdomains: []
        },

        OSM: {
          Normal: {
            Map: "//{s}.tile.osm.org/{z}/{x}/{y}.png",
          },
          Subdomains: ['a', 'b', 'c']
        },

        Baidu: {
          Normal: {
            Map: '//online{s}.map.bdimg.com/onlinelabel/?qt=tile&x={x}&y={y}&z={z}&styles=pl&scaler=1&p=1'
          },
          Satellite: {
            Map: '//shangetu{s}.map.bdimg.com/it/u=x={x};y={y};z={z};v=009;type=sate&fm=46',
            Annotion: '//online{s}.map.bdimg.com/tile/?qt=tile&x={x}&y={y}&z={z}&styles=sl&v=020'
          },
          Subdomains: '0123456789',
          tms: true
        }
      };
    },
    loadMap() {
      let map = L.map("map", {
        center: this.centerP, // 地图中心
        zoom: 12, //缩放比列
        zoomControl: false, //禁用 + - 按钮
        doubleClickZoom: false, // 禁用双击放大
        attributionControl: false, // 移除右下角leaflet标识
      });

      let normalm = L.tileLayer.chinaProvider("TianDiTu.Normal.Map", {
            maxZoom: 19,
            minZoom: 5,
            //tileSize: 512,
          }),
          normala = L.tileLayer.chinaProvider("TianDiTu.Normal.Annotion", {
            maxZoom: 19,
            minZoom: 5,
            //tileSize: 512,
          }),
          imgm = L.tileLayer.chinaProvider("TianDiTu.Satellite.Map", {
            maxZoom: 19,
            minZoom: 5,
            //tileSize: 512,
          }),
          imga = L.tileLayer.chinaProvider("TianDiTu.Satellite.Annotion", {
            maxZoom: 19,
            minZoom: 5,
            //tileSize: 512,
          });

      let OpenStreetMap_Mapnik = L.tileLayer(
          "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
          {
            maxZoom: 19,
            //tileSize: 512,
            attribution:
                '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
          }
      );
      let gray = L.tileLayer.chinaProvider('Geoq.Normal.Gray', {
        maxZoom: 19,
        minZoom: 5
      });
      let normal = L.layerGroup([normalm, normala]),
          image = L.layerGroup([imgm, imga]);
      //gray.addTo(map);
      let baseLayers = {
        灰色: gray,
        地图: normal,
        OSM: OpenStreetMap_Mapnik,
        影像: image,
      };
      let overlayLayers = {};
      L.control.layers(baseLayers, overlayLayers).addTo(map);
      L.control
        .zoom({
          zoomInTitle: "放大",
          zoomOutTitle: "缩小",
          position: "topright",
        })
        .addTo(map);

      this.manageLayerGroup(map);
      return map;
    },

    manageLayerGroup(map) {
      this.lineGroup
          .on("click", this.clickLayer)
          .on("mouseover", this.mouseoverLine)
          .on("mouseout", this.mouseoutLine)
          .addTo(map); //添加featureGroup统一管理交互
      for (let key in this.lineGroup._layers) {
        this.lineGroup._layers[key].on("pm:edit", (e) => {
          this.updatedLayers.add(key);
        });
      }
      this.heatLineGroup.addTo(map);
      this.heatLineGroup.on("pm:vertexclick", this.updateInEditMode());
      this.heatLineGroup.bindTooltip(() => {
        return "1号线<br>周边人口密度：" + (5656 + (this.popUpData.num++) * 100 + 234) +
            "人/平方公里<br> 周边房价均价：" + (23564 + this.popUpData.num * 345) +
            "元/平方米<br>在售房源：" + (63 + this.popUpData.num * 4) + "套"
      }, {
        interactive: true,
        sticky: true,
        className: 'leaflet-label' //自定义css控制
      });
      this.ribbonGroup.addTo(map);
      this.markerGroup.addTo(map);
    },

    mouseoverLine: function (e) {
      if (e.propagatedFrom.isPipe === true) {
        let selectedPipe = this.findPipe(e.layer.id);
        e.layer.setStyle({
          weight: selectedPipe.lineWeight + 3,
        });
      }
    },

    mouseoutLine: function (e) {
      if (e.propagatedFrom.isPipe === true) {
        let selectedPipe = this.findPipe(e.layer.id);
        e.layer.setStyle({
          weight: selectedPipe.lineWeight,
        });
      }
    },

    clickLayer: function (e) {
      if (this.mode === "normal" || this.notClickEvent === true) {
        return;
      }
      if (e.propagatedFrom.isPipe === true) {
        //若已经打开了编辑窗口，先关闭其他的窗口
        this.setPipelineInfoVisible(false);
        let layer = e.layer;
        layer.setStyle({
          weight: 5,
          dashArray: "",
          color: "#0000ff",
          fillOpacity: 0.7,
        });
        this.formTemp = this.findPipe(layer.id);
        console.log(this.formTemp)
        this.setEditingPipeline(this.formTemp);
        this.setPipelineInfoVisible(true);
      } else {
        this.editingMarker = this.findMarker(e.layer.id);
        this.setEditingMarker(this.editingMarker);
        this.setMarkerInfoVisible(true);
      }
    },

    initGeoman(map) {
      //L.PM.setOptIn(true);
      this.changeGeomanDefaultIcon(map);
      map.pm.setLang("zh");
      map.pm.setGlobalOptions({ layerGroup: this.lineGroup });
      // add leaflet-geoman controls with some options to the map
      map.pm.setPathOptions({
        //组件绘制的颜色
        color: "blue",
        weight: 2,
        fillColor: "#FF6666",
        fillOpacity: 0.4,
      });
      map.pm.addControls({
        position: "topleft",
        drawCircleMarker: false,
        drawRectangle: false,
        drawCircle: false,
        drawPolygon: false,
        cutPolygon: false,
      });

      map.on("pm:create", this.onLayerCreated());
      //map.on("pm:drawend", this.onLayerCreated2());
      map.on("pm:globaldrawmodetoggled", this.exitDrawMode());
      map.on("pm:globaleditmodetoggled", this.batchUpdate());
      //this.lineGroup.on("pm:vertexclick", this.updateInEditMode());
      map.on("pm:remove", this.onLayerRemove());
    },
  },
  mounted() {
    this.changeTianDiTuKey();
    this.map = this.loadMap();
    this.flushData();
    this.$store.commit("map/SET_LEAFLET_MAP", this.map);
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.cd-span {
  color: seagreen;
}

#map {
  width: 100%;
  height: 100%;
}

.leaflet-container {
  background: white;
}


</style>
