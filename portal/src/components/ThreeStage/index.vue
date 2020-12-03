<template>
  <!-- 挂载点 -->
  <div style="width: 100%; height: 100%; margin: 0; border: 0; padding: 0;">
    <canvas v-if="suportWebGL" ref="stage" style="width: 100%; height: 100%;"></canvas>
    <div v-else>
      <slot>浏览器不支持WebGL</slot>
    </div>
  </div>
</template>
<script>
import "./plugins/OrbitControls";
import "./plugins/OBJLoader";
import "./plugins/FBXLoader";

import {
  CubeTextureLoader,
  Scene,
  WebGLRenderer,
  PerspectiveCamera,
  AmbientLight,
  DirectionalLight,
  OrbitControls
} from "three";

//判断浏览器是否支持WebGL
const suportWebGL = (() => {
  try {
    var canvas = document.createElement("canvas");
    return !!(
      window.WebGLRenderingContext &&
      (canvas.getContext("webgl") || canvas.getContext("experimental-webgl"))
    );
  } catch (e) {
    return false;
  }
})();

export default {
  props: ["componentsInfo", "cameraControll"],
  data() {
    return {
      //requestAnimationFrame ID
      reqId: null,
      //浏览器是否支持WebGL
      suportWebGL,
      //相机
      camera: null,
      //场景
      scene: new Scene(),
      //相机控制器
      control: null
    };
  },
  //在DOM上树后进行初始化
  mounted() {
    //相机设置
    this.camera = new PerspectiveCamera(
      45,
      this.$el.offsetWidth / this.$el.offsetHeight,
      0.01,
      100000
    );
    //灯光
    this.scene.add(new AmbientLight(0xffffff));
    //天空盒
    this.scene.background = new CubeTextureLoader()
      .setPath("/img/skybox/")
      .load(["px.jpg", "nx.jpg", "py.jpg", "ny.jpg", "pz.jpg", "nz.jpg"]);
    //相机控制器
    this.control = new OrbitControls(this.camera, this.$refs.stage);
    //固定视角
    this.camera.position.y = this.cameraControll.height;
    this.control.target.set(0, this.cameraControll.height, 0);
    this.control.minDistance = this.cameraControll.distance;
    this.control.maxDistance = this.cameraControll.distance;
    // this.control.maxPolarAngle = Math.PI / 2;
    // this.control.minPolarAngle = Math.PI / 2;
    this.control.enablePan = false;
    this.control.enableKeys = false;
    this.control.update();
    //初始化渲染器
    this.renderer = new WebGLRenderer({
      antialias: true,
      preserveDrawingBuffer: true,
      alpha: true,
      canvas: this.$refs.stage
    });
    this.onResize();
    //浏览器自动重绘
    this.animate();
    //窗口进行拉伸需要更新
    window.addEventListener("resize", this.onResize, false);

    // var grid = new GridHelper(20, 20, 0x0000ff, 0xff0000);
    // grid.material.opacity = 0.2;
    // grid.material.transparent = true;
    // this.scene.add(grid);
  },
  //在组件销毁前要取消各种监听
  beforeDestroy() {
    //取消窗口拉伸监听
    window.removeEventListener("resize", this.onResize, false);
    //取消浏览器自动重绘
    cancelAnimationFrame(this.reqId);
  },
  methods: {
    //窗口拉伸触发事件
    onResize() {
      let width = this.$el.offsetWidth,
        height = this.$el.offsetHeight;
      //相机更新
      this.camera.aspect = width / height;
      this.camera.updateProjectionMatrix();
      //渲染器更新
      this.renderer.setSize(width, height);
      this.renderer.setPixelRatio(window.devicePixelRatio || 1);
    },
    //页面刷新重绘方法
    animate() {
      this.reqId = requestAnimationFrame(this.animate);
      this.renderer.render(this.scene, this.camera);
    },
    updateScene() {
      if (this.componentsInfo != null) {
        this.componentsInfo.map(item => {
          let modelIndex = item.modelSelected;
          let textureIndex = item.textureSelected;
          let modelObj = null;
          if (item.models[modelIndex] != null) {
            modelObj = item.models[modelIndex].obj;
          }
          let textureObj = null;
          if (item.textures[textureIndex] != null) {
            textureObj = item.textures[textureIndex].obj;
          }
          if (modelObj != null) {
            if (textureObj != null) {
              modelObj.traverse(child => {
                if (child.isMesh) {
                  //贴图放大
                  textureObj.repeat.set(
                    this.cameraControll.textureSize,
                    this.cameraControll.textureSize
                  );
                  child.material.map = textureObj;
                }
              });
            }
            item.models.map((subItem, index) => {
              if (subItem.obj != null) {
                // if (subItem.obj.parent == null) {
                //   this.scene.add(subItem.obj);
                // }
                this.scene.add(subItem.obj);
                if (index == modelIndex) {
                  subItem.obj.visible = true;
                } else {
                  subItem.obj.visible = false;
                }
              }
            });
          }
        });
      }
    },
    //获取当前的缩略图，并且返回为File
    getCover() {
      var dataurl = this.$refs.stage.toDataURL("image/jpeg", 0.2);
      var arr = dataurl.split(","),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n);
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
      }
      return new File([u8arr], "cover.jpg", { type: mime });
    }
  },
  watch: {
    cameraControll: {
      handler() {
        if (!isNaN(this.cameraControll.height)) {
          this.camera.position.y = this.cameraControll.height;
          this.control.target.set(0, this.cameraControll.height, 0);
          this.control.update();
        }
        if (!isNaN(this.cameraControll.distance)) {
          this.control.minDistance = this.cameraControll.distance;
          this.control.maxDistance = this.cameraControll.distance;
          this.control.update();
        }
        if (!isNaN(this.cameraControll.textureSize)) {
          this.updateScene();
        }
      },
      deep: true
    }
  }
};
</script>

