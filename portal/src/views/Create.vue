<template>
  <v-app dark id="keep">
    <!-- 侧边栏开始 -->
    <v-navigation-drawer v-model="mainDrawer" absolute fixed clipped app>
      <v-list dense>
        <v-layout row align-center>
          <v-flex xs6>
            <v-subheader>所有部件</v-subheader>
          </v-flex>
          <v-flex xs6 class="text-xs-right">
            <v-btn small flat @click="componentDialog=true">编辑</v-btn>
          </v-flex>
        </v-layout>
        <!-- 部件列表开始 -->
        <v-list-group v-for="(item,index) in componentsInfo" :key="index">
          <template v-slot:activator>
            <v-list-tile>
              <v-list-tile-content>
                <v-list-tile-title>{{ item.componentName }}</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
          </template>
          <v-list-tile
            color="amber lighten-1"
            v-for="(subItem,subIndex) in item.models"
            :key="subItem.fid"
            @click="modelChange(index,subIndex)"
          >
            <v-list-tile-content>
              <v-list-tile-title>
                {{ subItem.name }}&nbsp;
                <v-icon small v-if="subIndex==item.modelSelected">arrow_left</v-icon>
              </v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-divider/>
          <v-container grid-list-sm fluid v-if="item.textures.length>0">
            <v-layout row wrap>
              <v-flex v-for="(subItem,subIndex) in item.textures" :key="subItem.fid" xs2 d-flex>
                <v-card flat tile class="d-flex" @click="textureChange(index,subIndex)">
                  <v-img :src="subItem.path+'?size=60'" aspect-ratio="1" class="grey lighten-2">
                    <template v-slot:placeholder>
                      <v-layout fill-height align-center justify-center ma-0>
                        <v-progress-circular indeterminate color="grey lighten-5"></v-progress-circular>
                      </v-layout>
                    </template>
                  </v-img>
                </v-card>
              </v-flex>
            </v-layout>
          </v-container>
          <v-list-tile @click="componentIndex=index;waitUploadList=[];uploadDrawer = true">
            <v-list-tile-action>
              <v-icon>cloud_upload</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>上传模型文件</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-list-tile @click="componentIndex=index;textureDialog=true">
            <v-list-tile-action>
              <v-icon>picture_in_picture</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>贴图文件管理</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
        </v-list-group>
        <!-- 部件列表结束 -->
        <v-list-tile @click="addComponentDialog=true">
          <v-list-tile-action>
            <v-icon>add</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>添加新部件</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
      <!-- 相机参数 -->
      <v-list two-line>
        <v-list-tile>
          <v-layout row>
            <v-flex class="pr-3">
              <v-slider
                label="相机高度"
                v-model="cameraControll.height"
                :max="cameraControll.heightMax"
                min="0"
              ></v-slider>
            </v-flex>
          </v-layout>
        </v-list-tile>
        <v-list-tile>
          <v-layout row>
            <v-flex class="pr-3">
              <v-slider
                label="相机距离"
                v-model="cameraControll.distance"
                :max="cameraControll.distanceMax"
                min="0"
              ></v-slider>
            </v-flex>
          </v-layout>
        </v-list-tile>
        <v-list-tile>
          <v-layout row>
            <v-flex class="pr-3">
              <v-slider
                label="贴图缩放"
                v-model="cameraControll.textureSize"
                max="100"
                min="1"
              ></v-slider>
            </v-flex>
          </v-layout>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <!-- 侧边栏结束 -->
    <!-- 工具栏开始 -->
    <v-toolbar dark color="red" app clipped-left>
      <v-toolbar-side-icon @click="mainDrawer = !mainDrawer"></v-toolbar-side-icon>
      <span class="title ml-3 mr-5">
        V3D&nbsp;
        <span class="font-weight-light">创建方案</span>
      </span>
      <v-spacer/>
      <v-text-field v-model="name" solo-inverted flat hide-details class="hidden-sm-and-down"></v-text-field>
      <v-spacer/>
      <v-toolbar-items>
        <v-btn icon>
          <v-icon @click="settingDialog=true">settings</v-icon>
        </v-btn>
        <v-btn icon>
          <v-icon @click="refresh">refresh</v-icon>
        </v-btn>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-btn icon v-on="on" @click="saveScheme">
              <v-icon>save</v-icon>
            </v-btn>
          </template>
          <span>保存方案</span>
        </v-tooltip>
        <v-btn icon @click="exitDialog = true">
          <v-icon>exit_to_app</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>
    <!-- 工具栏结束 -->
    <!-- 诡异的样式问题 -->
    <v-content style="height:0px;background-color:#ddd">
      <ThreeStage
        :components-info="componentsInfo"
        :camera-controll="cameraControll"
        ref="threeStage"
      />
    </v-content>
    <!-- 右侧drawer开始 -->
    <v-navigation-drawer v-model="uploadDrawer" right temporary fixed>
      <input
        type="file"
        multiple="multiple"
        ref="file"
        style="display:none"
        accept=".fbx, .obj"
        @change="fileChange"
      >
      <v-list>
        <v-subheader
          v-if="componentsInfo[componentIndex]"
        >{{componentsInfo[componentIndex].componentName}} - 待上传文件</v-subheader>
        <div v-for="(item,index) in waitUploadList" :key="index">
          <v-list-tile>
            <v-list-tile-content>
              <v-list-tile-title v-text="item.name"></v-list-tile-title>
              <v-list-tile-sub-title>{{item.size|fileSize}}</v-list-tile-sub-title>
            </v-list-tile-content>
            <v-list-tile-action>
              <v-btn icon ripple @click="waitUploadList.splice(index, 1)">
                <v-icon color="grey lighten-1">cancel</v-icon>
              </v-btn>
            </v-list-tile-action>
          </v-list-tile>
          <v-divider></v-divider>
        </div>
        <v-list-tile>
          <v-list-tile-content>
            <v-btn block dark color="blue" @click="$refs.file.click()">
              选择文件
              <v-icon right dark>folder_open</v-icon>
            </v-btn>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile>
          <v-list-tile-content>
            <v-btn block dark color="red" @click="uploadModel">
              开始上传
              <v-icon right dark>cloud_upload</v-icon>
            </v-btn>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <!-- 右侧drawer结束 -->
    <!-- 贴图管理 开始 -->
    <v-dialog v-model="textureDialog" persistent max-width="600px">
      <input
        type="file"
        multiple="multiple"
        ref="textureFile"
        style="display:none"
        accept=".jpg, .png"
        @change="textureFileChange"
      >
      <v-card light>
        <v-card-title>
          <span
            class="headline"
            v-if="componentsInfo[componentIndex]"
          >{{componentsInfo[componentIndex].componentName}} - 贴图管理</span>
        </v-card-title>
        <v-card-text>
          <v-container grid-list-sm fluid v-if="componentsInfo[componentIndex]">
            <v-layout row wrap>
              <v-flex
                v-for="(item,index) in componentsInfo[componentIndex].textures"
                :key="item.name"
                xs1
                d-flex
                @click="delTexture(componentIndex,index)"
              >
                <v-card flat tile class="d-flex elevation-1">
                  <v-img :src="item.path+'?size=40'" aspect-ratio="1" class="grey lighten-2">
                    <template v-slot:placeholder>
                      <v-layout fill-height align-center justify-center ma-0>
                        <v-progress-circular indeterminate color="grey lighten-5"></v-progress-circular>
                      </v-layout>
                    </template>
                  </v-img>
                </v-card>
              </v-flex>
            </v-layout>
          </v-container>
          <small style="display:block;text-align:right;color:#aaa" class="pr-4">*单击可删除贴图</small>
        </v-card-text>
        <v-card-actions>
          <v-btn color="blue" flat @click="$refs.textureFile.click()">添加贴图</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="red" flat @click="textureDialog = false">确定</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- 贴图管理 结束 -->
    <!-- 部件管理 开始 -->
    <v-dialog v-model="componentDialog" max-width="600px">
      <v-card light>
        <v-card-text>
          <v-tabs v-model="tabActive" color="red" dark slider-color="yellow">
            <v-tab v-for="(item,index) in componentsInfo" :key="index" ripple>{{item.componentName}}</v-tab>
            <v-tab-item v-for="(item,index) in componentsInfo" :key="index">
              <v-card flat>
                <v-card-text>
                  <v-list>
                    <div v-for="(subItem,subIndex) in item.models" :key="subItem.fid">
                      <v-list-tile>
                        <v-list-tile-content>
                          <v-text-field
                            v-model="componentsInfo[index].models[subIndex].name"
                            single-line
                            full-width
                            hide-details
                          ></v-text-field>
                        </v-list-tile-content>
                        <v-list-tile-content class="align-end">
                          <v-btn icon ripple @click="delModel(index, subIndex)">
                            <v-icon color="grey lighten-1">cancel</v-icon>
                          </v-btn>
                        </v-list-tile-content>
                      </v-list-tile>
                      <v-divider/>
                    </div>
                  </v-list>
                </v-card-text>
              </v-card>
              <div class="text-xs-center">
                <v-btn round color="primary" outline @click="delComponent(index)">删除该部件</v-btn>
              </div>
            </v-tab-item>
          </v-tabs>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="red"
            flat
            @click="componentDialog = false"
            item-text="componentName"
            item-children="models"
          >确定</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- 部件管理 结束 -->
    <!-- 添加部件模态框 开始-->
    <v-dialog v-model="addComponentDialog" width="500">
      <v-card>
        <v-toolbar dark color="red">
          <v-toolbar-title>添加新部件</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon dark @click="addComponentDialog = false">
            <v-icon>close</v-icon>
          </v-btn>
        </v-toolbar>
        <v-card-text>
          <v-text-field label="部件名称" color="red" v-model="componentName"></v-text-field>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn dark flat @click="addComponent">添加</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- 添加部件模态框  结束 -->
    <!-- 文件上传进度模态框 开始 -->
    <v-dialog v-model="uploadingDialog" persistent width="300">
      <v-card color="red" dark>
        <v-card-text>
          文件上传中
          <v-progress-linear indeterminate color="white" class="mb-0"></v-progress-linear>
        </v-card-text>
      </v-card>
    </v-dialog>
    <!-- 文件上传进度模态框 结束-->
    <!-- 文件加载进度模态框 开始 -->
    <v-dialog v-model="downloadingDialog" persistent width="300">
      <v-card color="red" dark>
        <v-card-text>
          文件加载中
          <v-progress-linear indeterminate color="white" class="mb-0"></v-progress-linear>
        </v-card-text>
      </v-card>
    </v-dialog>
    <!-- 文件加载进度模态框 结束-->
    <!-- 退出保存模态框 开始 -->
    <v-dialog v-model="exitDialog" width="300">
      <v-card>
        <v-card-text>是否保存当前修改？</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn flat @click="$router.push('/home')">直接退出</v-btn>
          <v-btn flat @click="saveScheme();$router.push('/home')">保存退出</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- 退出保存模态框 结束 -->
    <!-- 设置开始 -->
    <v-dialog v-model="settingDialog" fullscreen hide-overlay transition="dialog-bottom-transition">
      <v-card light>
        <v-toolbar dark color="primary">
          <v-btn icon dark @click="settingDialog = false">
            <v-icon>close</v-icon>
          </v-btn>
          <v-toolbar-title>设置</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-toolbar-items>
            <v-btn dark flat @click="saveSetting">确定</v-btn>
          </v-toolbar-items>
        </v-toolbar>
        <v-list three-line subheader>
          <v-subheader>相机参数</v-subheader>
          <v-list-tile avatar>
            <v-list-tile-content>
              <v-list-tile-title>相机最大高度</v-list-tile-title>
              <v-list-tile-sub-title>
                <v-flex shrink style="width: 60px">
                  <v-text-field
                    v-model="cameraControll.heightMax"
                    class="mt-0"
                    hide-details
                    single-line
                    type="number"
                  ></v-text-field>
                </v-flex>
              </v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-list-tile avatar>
            <v-list-tile-content>
              <v-list-tile-title>相机最远距离</v-list-tile-title>
              <v-list-tile-sub-title>
                <v-flex shrink style="width: 60px">
                  <v-text-field
                    v-model="cameraControll.distanceMax"
                    class="mt-0"
                    hide-details
                    single-line
                    type="number"
                  ></v-text-field>
                </v-flex>
              </v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-divider></v-divider>
          <v-subheader>方案分享</v-subheader>
          <v-layout wrap class="ma-3">
            <v-flex xs12 sm8 class="pr-5">
              <v-switch label="开启分享" color="red" v-model="isShared"></v-switch>
              <v-text-field label="访问地址" outline readonly :value="shareLink"></v-text-field>
              <input :value="shareLink" ref="shareLink" style="display:none">
              <input
                :value="`<iframe src='${shareLink}' scrolling='no' frameborder='0' width='100%' height='100%'></iframe>`"
                ref="iframLink"
                style="display:none"
              >
              <v-switch label="使用密码" color="red" v-model="hasPassword"></v-switch>
              <v-text-field
                v-model="sharePassword"
                label="访问密码"
                :type="hasPassword&&showPassword ? 'text' : 'password'"
                :disabled="!hasPassword"
                :append-icon="hasPassword&&showPassword? 'visibility' : 'visibility_off'"
                @click:append="showPassword = !showPassword"
              ></v-text-field>
            </v-flex>
            <v-flex xs12 sm4>
              <v-layout justify-end fill-height>
                <v-flex align-self-end>
                  <canvas ref="qr"></canvas>
                </v-flex>
                <v-spacer/>
                <v-layout column justify-end class="mb-3">
                  <a style="text-align:right" class="ma-2">
                    关于IFrames地址的使用
                    <v-icon small>help</v-icon>
                  </a>
                  <v-btn color="blue" dark @click="copyIframe">iframe地址</v-btn>
                  <v-btn color="blue" dark @click="copyLink">复制访问地址</v-btn>
                  <v-btn color="green" dark @click="saveQR">保存二维码</v-btn>
                </v-layout>
              </v-layout>
            </v-flex>
          </v-layout>
        </v-list>
      </v-card>
    </v-dialog>
    <!-- 设置结束 -->
  </v-app>
</template>
<script>
import "../plugins/OBJLoader";
import "../plugins/FBXLoader";
import {
  LoadingManager,
  TextureLoader,
  OBJLoader,
  FBXLoader,
  DoubleSide
} from "three";
import ThreeStage from "../components/ThreeStage";
import QRious from "qrious";

export default {
  components: {
    ThreeStage
  },
  props: ["sid"],
  data: () => ({
    //tab
    tabActive: null,
    //drawer
    mainDrawer: false,
    uploadDrawer: false,
    //dialog
    addComponentDialog: null,
    uploadingDialog: null,
    downloadingDialog: null,
    exitDialog: null,
    textureDialog: null,
    settingDialog: null,
    componentDialog: null,
    //方案临时数据
    componentIndex: null,
    componentName: "",
    waitUploadList: [],
    //方案保存数据
    name: "",
    cameraControll: {
      height: 10,
      distance: 40,
      textureSize:1,
      heightMax: 200,
      distanceMax: 200
    },
    componentsInfo: [],
    //share
    shareLink: "",
    isShared: false,
    hasPassword: false,
    showPassword: false,
    sharePassword: ""
  }),
  watch: {
    //当创建dialog退出时候清空文本栏
    addComponentDialog() {
      this.componentName = "";
    }
  },
  filters: {
    fileSize: function(value) {
      return (value / 1024 / 1024).toFixed(2) + " MB";
    }
  },
  methods: {
    saveSetting() {
      let share = 0;
      if (this.isShared) {
        if (this.hasPassword) {
          share = 2;
        } else {
          share = 1;
        }
      }
      this.$http
        .post(
          "/scheme/share",
          {
            sid: this.sid,
            share: share,
            password: this.sharePassword
          },
          {}
        )
        .then(
          data => {
            console.info(data);
          },
          error => {
            console.info(error);
          }
        );
      this.settingDialog = false;
    },
    copyLink() {
      this.$refs.shareLink.style.display = "inline";
      this.$refs.shareLink.select();
      document.execCommand("Copy");
      this.$refs.shareLink.style.display = "none";
    },
    copyIframe() {
      this.$refs.iframLink.style.display = "inline";
      this.$refs.iframLink.select();
      document.execCommand("Copy");
      this.$refs.iframLink.style.display = "none";
    },
    saveQR() {
      let a = document.createElement("a");
      a.href = this.$refs.qr.toDataURL("image/png");
      a.download = "qr.png";
      let event = document.createEvent("MouseEvents");
      event.initEvent("click", true, false);
      a.dispatchEvent(event);
    },
    addComponent() {
      let componentName = this.componentName.replace(/\s+/g, "");
      if (componentName.length > 0)
        this.componentsInfo.push({
          componentName,
          modelSelected: -1,
          textureSelected: -1,
          models: [],
          textures: []
        });
      this.addComponentDialog = false;
    },
    fileChange() {
      for (var i = 0, j = this.$refs.file.files.length; i < j; i++) {
        this.waitUploadList.push(this.$refs.file.files[i]);
      }
      //重置file input
      this.$refs.file.value = "";
    },
    textureFileChange() {
      if (this.$refs.textureFile.files.length > 0) {
        this.uploadingDialog = true;
        var formData = new FormData();
        formData.append("sid", this.sid);
        for (var i = 0, j = this.$refs.textureFile.files.length; i < j; i++) {
          formData.append("files", this.$refs.textureFile.files[i]);
        }
        this.$http
          .post("/file/uploadTexture", formData, {
            headers: { "Content-Type": "multipart/form-data" }
          })
          .then(
            data => {
              console.info(data);
              if (data.body.code == 0) {
                data.body.data.uploadReply.map(item => {
                  this.componentsInfo[this.componentIndex].textures.push(item);
                });
              }
              this.uploadingDialog = false;
            },
            error => {
              console.info(error);
            }
          );
        //重置file input
        this.$refs.textureFile.value = "";
      }
    },
    uploadModel() {
      if (this.waitUploadList.length > 0) {
        this.uploadDrawer = false;
        this.uploadingDialog = true;
        var formData = new FormData();
        formData.append("sid", this.sid);
        this.waitUploadList.map(item => {
          formData.append("files", item);
        });
        this.$http
          .post("/file/uploadModel", formData, {
            headers: { "Content-Type": "multipart/form-data" }
          })
          .then(
            data => {
              console.info(data);
              if (data.body.code == 0) {
                data.body.data.uploadReply.map(item => {
                  this.componentsInfo[this.componentIndex].models.push(item);
                });
              }
              this.uploadingDialog = false;
            },
            error => {
              console.info(error);
            }
          );
      }
    },
    refresh() {
      this.$http.get("/scheme/" + this.sid, {}, {}).then(
        data => {
          console.info(data);
          if (data.body.code == 0) {
            this.name = data.body.data.name;
            this.shareLink = data.body.data.shareLink;
            this.sharePassword = data.body.data.password;
            var qr = new QRious({
              element: this.$refs.qr,
              size: 250,
              level: "L",
              foreground: "#424242",
              background: "#fff",
              value: this.shareLink
            });
            switch (data.body.data.share) {
              case 0:
                this.isShared = false;
                this.hasPassword = false;
                break;
              case 1:
                this.isShared = true;
                this.hasPassword = false;
                break;
              case 2:
                this.isShared = true;
                this.hasPassword = true;
                break;
            }
            let componentsInfo = JSON.parse(data.body.data.data);
            let cameraControll = JSON.parse(data.body.data.camera);
            if (componentsInfo) this.componentsInfo = componentsInfo;
            if (cameraControll) this.cameraControll = cameraControll;
            let manager = new LoadingManager(() => {
              this.downloadingDialog = false;
            });
            this.componentsInfo.map((item, index) => {
              let modelIndex = item.modelSelected;
              let textureIndex = item.textureSelected;

              if (item.models[modelIndex] != null) {
                this.downloadingDialog = true;
                this.modelChange(index, modelIndex, manager);
              }
              if (item.textures[textureIndex] != null) {
                this.downloadingDialog = true;
                this.textureChange(index, textureIndex, manager);
              }
            });
          } else if (data.body.code == 2) {
            this.$router.push("/");
          } else {
            this.$router.push("/home");
          }
        },
        error => {
          console.info(error);
        }
      );
    },
    saveScheme() {
      var formData = new FormData();
      formData.append("sid", this.sid);
      formData.append("name", this.name);
      formData.append("file", this.$refs.threeStage.getCover());
      //清空缓存的obj
      formData.append(
        "data",
        JSON.stringify(this.componentsInfo, (key, value) => {
          if (key == "obj") {
            return null;
          } else {
            return value;
          }
        })
      );
      formData.append("camera", JSON.stringify(this.cameraControll));
      this.$http
        .post("/scheme/save", formData, {
          headers: { "Content-Type": "multipart/form-data" }
        })
        .then(
          data => {
            console.info(data);
          },
          error => {
            console.info(error);
          }
        );
    },
    modelChange(componentIndex, modelIndex, manager) {
      if (manager == null) {
        this.downloadingDialog = true;
        manager = new LoadingManager(() => {
          this.downloadingDialog = false;
        });
      }
      let model = this.componentsInfo[componentIndex].models[modelIndex];
      if (model.obj == null) {
        //obj
        if (
          model.path
            .split(".")
            .pop()
            .toLowerCase() === "obj"
        ) {
          new OBJLoader(manager).load(
            model.path,
            //success回调
            obj => {
              obj.traverse(child => {
                if (child.isMesh) {
                  child.material.side = DoubleSide;
                }
              });
              model.obj = obj;
              this.componentsInfo[componentIndex].modelSelected = modelIndex;
              this.$refs.threeStage.updateScene();
            }
          );
          //fbx
        } else if (
          model.path
            .split(".")
            .pop()
            .toLowerCase() === "fbx"
        ) {
          new FBXLoader(manager).load(
            model.path,
            obj => {
              obj.traverse(child => {
                if (child.isMesh) {
                  child.material.side = DoubleSide;
                }
              });
              model.obj = obj;
              this.componentsInfo[componentIndex].modelSelected = modelIndex;
              this.$refs.threeStage.updateScene();
            },
            xhr => {
              if (xhr.lengthComputable) {
                // console.info((xhr.loaded / xhr.total) * 100);
              }
            }
          );
        }
      } else {
        this.componentsInfo[componentIndex].modelSelected = modelIndex;
        this.$refs.threeStage.updateScene();
        this.downloadingDialog = false;
      }
    },
    textureChange(componentIndex, textureIndex, manager) {
      if (manager == null) {
        this.downloadingDialog = true;
        manager = new LoadingManager(() => {
          this.downloadingDialog = false;
        });
      }
      let texture = this.componentsInfo[componentIndex].textures[textureIndex];
      if (texture.obj == null) {
        new TextureLoader(manager).load(texture.path, obj => {
          texture.obj = obj;
          this.componentsInfo[componentIndex].textureSelected = textureIndex;
          this.$refs.threeStage.updateScene();
        });
      } else {
        this.componentsInfo[componentIndex].textureSelected = textureIndex;
        this.$refs.threeStage.updateScene();
        this.downloadingDialog = false;
      }
    },
    delTexture(componentIndex, textureIndex) {
      this.componentsInfo[componentIndex].textures.splice(textureIndex, 1);
    },
    delModel(componentIndex, modelIndex) {
      let model = this.componentsInfo[componentIndex].models[modelIndex].obj;
      if (model != null) {
        model.parent.remove(model);
      }
      this.componentsInfo[componentIndex].models.splice(modelIndex, 1);
    },
    delComponent(componentIndex) {
      for (let component of this.componentsInfo[componentIndex].models) {
        if (component.obj != null) {
          component.obj.parent.remove(component.obj);
        }
      }
      this.componentsInfo.splice(componentIndex, 1);
    }
  },
  created() {
    this.refresh();
  }
};
</script>
<style >
</style>