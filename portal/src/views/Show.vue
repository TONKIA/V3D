<template>
  <v-app>
    <v-toolbar color="red" dark app clipped-left>
      <v-toolbar-side-icon @click="mainDrawer = !mainDrawer"></v-toolbar-side-icon>
      <v-toolbar-title>{{name}}</v-toolbar-title>
    </v-toolbar>
    <v-content style="height:0px;background-color:#ddd">
      <ThreeStage
        :components-info="componentsInfo"
        :camera-controll="cameraControll"
        ref="threeStage"
      />
      <v-speed-dial
        v-model="fab"
        bottom
        right
        direction="top"
        transition="slide-y-reverse-transition"
      >
        <template v-slot:activator>
          <v-btn v-model="fab" color="red" dark fab>
            <v-icon>add</v-icon>
            <v-icon>close</v-icon>
          </v-btn>
        </template>
        <v-btn fab dark small color="blue" @click="pulishDialog=true">
          <v-icon>edit</v-icon>
        </v-btn>
        <v-btn fab dark small color="green" @click="getComment();commentDialog=true">
          <v-icon>comment</v-icon>
        </v-btn>
      </v-speed-dial>
    </v-content>
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
    <!-- 密码 开始-->
    <v-dialog v-model="passwordDialog" persistent max-width="290">
      <v-card>
        <v-card-title class="headline">该项目需要密码访问</v-card-title>
        <v-card-text>
          <v-text-field label="密码" outline v-model="password" type="password"></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" flat @click="customer">确定</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- 密码 结束-->
    <!-- 发表评论 开始 -->
    <v-dialog v-model="pulishDialog" max-width="500px">
      <v-card>
        <v-card-title>发表评论</v-card-title>
        <v-divider/>
        <v-card-text>
          <v-text-field single-line outline v-model="text"></v-text-field>
        </v-card-text>
        <v-divider/>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" flat @click="pulish">发表</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- 发表评论 结束 -->
    <!-- 评论区 开始 -->
    <v-dialog v-model="commentDialog" scrollable max-width="800px">
      <v-card>
        <v-card-title class="font-weight-bold headline">评论区</v-card-title>
        <v-divider></v-divider>
        <v-card-text style="height: 300px;">
          <v-list two-line>
            <div v-for="item in commentList" :key="item.time">
              <v-list-tile>
                <v-list-tile-content>
                  <v-list-tile-title>{{ item.text }}</v-list-tile-title>
                  <v-list-tile-sub-title
                    class="text--primary text-xs-right"
                  >{{ item.time|timeFilter }}</v-list-tile-sub-title>
                </v-list-tile-content>
              </v-list-tile>
              <v-divider/>
            </div>
          </v-list>
        </v-card-text>
      </v-card>
    </v-dialog>
    <!-- 评论区 结束 -->
    <v-navigation-drawer v-model="mainDrawer" absolute fixed clipped app>
      <v-tabs v-model="tab" color="red" dark align-with-title show-arrows>
        <v-tabs-slider color="yellow"></v-tabs-slider>
        <v-tab v-for="(item,index) in componentsInfo" :key="index" ripple>{{item.componentName}}</v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab" style="position:absolute;buttom:0px;width:100%">
        <v-tab-item v-for="(item,index) in componentsInfo" :key="index">
          <v-card flat>
            <v-card-text>
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
            </v-card-text>
          </v-card>
        </v-tab-item>
      </v-tabs-items>
    </v-navigation-drawer>
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
export default {
  components: {
    ThreeStage
  },
  props: ["sid"],
  data() {
    return {
      commentList: null,
      text: "",
      mainDrawer: false,
      tab: null,
      fab: false,
      pulishDialog: null,
      commentDialog: null,
      downloadingDialog: null,
      passwordDialog: null,
      password: "",
      name: "",
      cameraControll: {
        height: 10,
        distance: 40,
        heightMax: 200,
        distanceMax: 200
      },
      componentsInfo: []
    };
  },
  methods: {
    getComment() {
      this.$http.post("/show/getComment", { sid: this.sid }, {}).then(
        data => {
          console.info(data);
          if (data.body.code == 0) {
            this.commentList = data.body.data;
          }
        },
        error => {
          console.info(error);
        }
      );
    },
    pulish() {
      this.$http
        .post("/show/comment", { sid: this.sid, text: this.text }, {})
        .then(
          data => {
            console.info(data);
            if (data.body.code == 0) {
              this.pulishDialog = false;
            }
          },
          error => {
            console.info(error);
          }
        );
    },
    getSchemeInfo() {
      this.$http.get("/scheme/" + this.sid, {}, {}).then(
        data => {
          console.info(data);
          if (data.body.code == 0) {
            this.name = data.body.data.name;
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
    customer() {
      this.$http
        .post(
          "/show/customer",
          {
            sid: this.sid,
            password: this.password
          },
          {}
        )
        .then(
          data => {
            console.info(data);
            if (data.body.code == 0) {
              this.passwordDialog = false;
              this.password = "";
              this.getSchemeInfo();
            } else {
              this.password = "";
            }
          },
          error => {
            console.info(error);
          }
        );
    }
  },
  watch: {
    pulishDialog() {
      this.text = "";
    }
  },
  filters: {
    timeFilter: function(value) {
      var date = new Date();
      date.setTime(value);
      var month =
        date.getMonth().toString().length > 1
          ? date.getMonth()
          : "0" + date.getMonth();
      var day =
        date.getDate().toString().length > 1
          ? date.getDate()
          : "0" + date.getDate();
      var hour =
        date.getHours().toString().length > 1
          ? date.getHours()
          : "0" + date.getHours();
      var min =
        date.getMinutes().toString().length > 1
          ? date.getMinutes()
          : "0" + date.getMinutes();
      return (
        date.getFullYear() + "-" + month + "-" + day + " " + hour + ":" + min
      );
    }
  },
  created() {
    this.$http.get("/show/" + this.sid, {}, {}).then(
      data => {
        if (data.body.code == 0) {
          console.info(data);
          let share = data.body.data.share;
          if (share == 1) {
            this.getSchemeInfo();
          } else if (share == 2) {
            this.passwordDialog = true;
          }else{
            this.$router.push("/refuse")
          }
        }
      },
      error => {
        console.info(error);
      }
    );
  }
};
</script>

<style>
.v-speed-dial {
  position: absolute;
}

.v-btn--floating {
  position: relative;
}
</style>
