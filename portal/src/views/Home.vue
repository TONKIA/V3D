<template>
  <v-app dark id="inspire">
    <!-- drawer开始 -->
    <v-navigation-drawer v-model="drawer" fixed app>
      <v-list class="pa-1">
        <v-list-tile avatar>
          <v-list-tile-avatar @click="avatarUpload=true">
            <img :src="avatar">
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title v-text="userName"></v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
      <v-list class="pt-0" dense>
        <v-divider></v-divider>
        <v-list-tile @click="userDialog=true">
          <v-list-tile-action>
            <v-icon>account_box</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>修改用户信息</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile @click>
          <v-list-tile-action>
            <v-icon>info</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>关于V3D</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile @click="logout">
          <v-list-tile-action>
            <v-icon>exit_to_app</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>退出系统</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <!-- drawer结束 -->

    <!-- 工具栏开始 -->
    <v-toolbar color="red" dark fixed app>
      <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-toolbar-title>V3D</v-toolbar-title>
      <v-spacer/>
      <v-text-field
        clearable
        v-model="keyword"
        solo-inverted
        flat
        hide-details
        label="搜索"
        prepend-inner-icon="search"
        class="hidden-sm-and-down"
      ></v-text-field>
      <v-spacer/>
      <v-toolbar-items class="hidden-sm-and-down">
        <v-btn flat @click="createDialog=true">
          <v-icon>add</v-icon>创建方案
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>
    <!-- 工具栏结束 -->

    <!-- 内容开始 -->
    <v-content>
      <v-container fluid fill-height v-if="schemeList.length==0">
        <v-layout justify-center align-center>
          <v-flex text-xs-center class="headline font-weight-light">还没有方案，请先创建方案</v-flex>
        </v-layout>
      </v-container>
      <v-container fluid grid-list-md v-else>
        <v-layout row wrap>
          <v-flex
            v-for="(item,index) in schemeList"
            :key="item.sid"
            xs12
            sm6
            md3
            v-if="keyword==null||item.name.indexOf(keyword)!=-1"
          >
            <v-card>
              <v-img :src="item.cover" height="200px">
                <v-container fill-height fluid pa-2>
                  <v-layout fill-height>
                    <v-flex xs12 align-end flexbox>
                      <span class="headline white--text" v-text="item.name"></span>
                    </v-flex>
                  </v-layout>
                </v-container>
              </v-img>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn icon @click="edit(item.sid)">
                  <v-icon>edit</v-icon>
                </v-btn>
                <v-btn icon @click="share(index)">
                  <v-icon>share</v-icon>
                </v-btn>
                <v-btn icon @click="del(index)">
                  <v-icon>delete</v-icon>
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
    <!-- 内容结束 -->

    <!-- 底部开始 -->
    <v-footer color="red" app>
      <v-flex py-3 text-xs-center white--text xs12>
        <span class="white--text">&copy; 2019 TONKIA V3D</span>
      </v-flex>
    </v-footer>
    <!-- 底部结束 -->

    <!-- 创建方案模态框 开始-->
    <v-dialog v-model="createDialog" width="500">
      <v-card>
        <v-toolbar dark color="red">
          <v-toolbar-title>创建新方案</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon dark @click="createDialog = false">
            <v-icon>close</v-icon>
          </v-btn>
        </v-toolbar>
        <v-card-text>
          <v-text-field label="方案名称" color="red" v-model="schemeName"></v-text-field>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn dark flat @click="createScheme">确定</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- 创建方案模态框 结束 -->

    <!-- 方案分享 开始 -->
    <v-dialog v-model="shareDialog" persistent max-width="600px">
      <v-card light>
        <v-card-title>
          <span class="headline">方案分享</span>
        </v-card-title>
        <v-divider/>
        <v-card-text>
          <v-container grid-list-md>
            <v-layout wrap>
              <v-flex xs12>
                <v-switch label="开启分享" color="red" v-model="isShared"></v-switch>
                <v-text-field label="访问地址" outline readonly :value="shareLink"></v-text-field>
                <input :value="shareLink" ref="shareLink" style="display:none">
                <input
                  :value="`<iframe src='${shareLink}' scrolling='no' frameborder='0' width='100%' height='100%'></iframe>`"
                  ref="iframLink"
                  style="display:none"
                >
              </v-flex>
              <v-layout class="ma-3">
                <canvas ref="qr"></canvas>
                <v-spacer/>
                <v-layout column justify-end>
                  <a style="text-align:right" class="ma-2">
                    关于IFrames地址的使用
                    <v-icon small>help</v-icon>
                  </a>
                  <v-btn color="blue" dark @click="copyIframe">iframe地址</v-btn>
                  <v-btn color="blue" dark @click="copyLink">复制访问地址</v-btn>
                  <v-btn color="green" dark @click="saveQR">保存二维码</v-btn>
                </v-layout>
              </v-layout>
              <v-flex xs12>
                <v-divider/>
              </v-flex>
              <v-flex xs12>
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
            </v-layout>
          </v-container>
        </v-card-text>
        <v-divider/>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" flat @click="shareDialog = false">取消</v-btn>
          <v-btn color="blue darken-1" flat @click="saveShare">保存</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- 方案分享 结束 -->

    <!-- 用户信息修改 开始 -->
    <v-dialog v-model="userDialog" persistent max-width="600px">
      <v-card light>
        <v-card-title>
          <span class="headline">用户信息修改</span>
        </v-card-title>
        <v-divider/>
        <v-card-text>
          <v-container>
            <v-layout row wrap>
              <v-flex xs9>
                <v-text-field box label="用户名" v-model="newName"></v-text-field>
              </v-flex>
              <v-flex xs3>
                <v-btn large flat color="blue" @click="changeName">修改用户名</v-btn>
              </v-flex>
              <v-flex xs12>
                <v-text-field box label="修改密码" v-model="pwd" type="password"></v-text-field>
              </v-flex>
              <v-flex xs12>
                <v-text-field box label="确认密码" v-model="repwd" type="password"></v-text-field>
              </v-flex>
              <v-flex xs12>
                <v-btn block color="blue" dark @click="changePwd">修改密码</v-btn>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>
        <v-divider/>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" flat @click="userDialog = false">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- 用户信息修改 结束 -->

    <!-- 图片上传组件 -->
    <avatar-upload
      @crop-upload-success="cropUploadSuccess"
      field="file"
      v-model="avatarUpload"
      :width="300"
      :height="300"
      url="/user/avatar"
      img-format="png"
    ></avatar-upload>
    <!-- 图片上传组件 -->
  </v-app>
</template>

<script>
import QRious from "qrious";
import avatarUpload from "vue-image-crop-upload";

export default {
  components: {
    "avatar-upload": avatarUpload
  },
  data: () => ({
    //方案数据
    schemeList: [],
    //临时数据
    schemeName: "",
    keyword: "",
    shareLink: "",
    isShared: false,
    hasPassword: false,
    showPassword: false,
    sharePassword: "",
    shareIndex: 0,
    //用户
    avatar: null,
    userName: null,
    //ui
    drawer: null,
    createDialog: null,
    shareDialog: null,
    userDialog: null,
    //crop image
    avatarUpload: false,
    //update userinfo
    newName: "",
    pwd: "",
    repwd: ""
  }),
  methods: {
    changeName() {
      this.$http
        .post(
          "/user/changeInfo",
          {
            userName: this.newName
          },
          {}
        )
        .then(
          data => {
            console.info(data);
            if (data.body.code == 0) {
              this.userName = this.newName;
              this.newName = "";
            }
          },
          error => {
            console.info(error);
          }
        );
    },
    changePwd() {
      if (this.pwd == this.repwd) {
        this.$http
          .post(
            "/user/changeInfo",
            {
              password: this.pwd
            },
            {}
          )
          .then(
            data => {
              console.info(data);
              if (data.body.code == 0) {
                this.pwd = "";
                this.repwd = "";
              }
            },
            error => {
              console.info(error);
            }
          );
      }
    },
    cropUploadSuccess(data, field) {
      if (data.code == 0) {
        this.avatar = data.data.avatar;
      }
    },
    del(index) {
      this.$http
        .post(
          "/scheme/delete",
          {
            sid: this.schemeList[index].sid
          },
          {}
        )
        .then(
          data => {
            console.info(data);
            if (data.body.code == 0) {
              this.schemeList.splice(index, 1);
            }
          },
          error => {
            console.info(error);
          }
        );
    },
    edit(sid) {
      this.$router.push("create/" + sid);
    },
    share(index) {
      this.shareIndex = index;
      let scheme = this.schemeList[index];
      var qr = new QRious({
        element: this.$refs.qr,
        size: 250,
        level: "L",
        foreground: "#424242",
        background: "#fff",
        value: scheme.shareLink
      });
      this.shareLink = scheme.shareLink;
      this.sharePassword = scheme.password;
      switch (scheme.share) {
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
      this.shareDialog = true;
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
    saveShare() {
      let index = this.shareIndex;
      let share = 0;
      if (this.isShared) {
        if (this.hasPassword) {
          share = 2;
        } else {
          share = 1;
        }
      }
      this.schemeList[index].share = share;
      this.schemeList[index].password = this.sharePassword;
      this.$http
        .post(
          "/scheme/share",
          {
            sid: this.schemeList[index].sid,
            share: this.schemeList[index].share,
            password: this.schemeList[index].password
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
      this.shareDialog = false;
    },
    createScheme() {
      this.$http
        .post(
          "/scheme/create",
          {
            name: this.schemeName
          },
          {}
        )
        .then(
          data => {
            console.info(data);
            if (data.body.code == 0) {
              this.$router.push("create/" + data.body.data.sid);
            }
          },
          error => {
            console.info(error);
          }
        );
    },
    logout() {
      this.$http.get("/user/logout").then(
        data => {
          console.info(data);
          if (data.body.code == 0) {
            this.$router.push("/");
          }
        },
        error => {
          console.info(error);
        }
      );
    }
  },
  watch: {
    //当创建dialog推出时候清空文本栏
    createDialog() {
      this.schemeName = "";
    },
    userDialog() {
      this.newName = "";
      this.pwd = "";
      this.repwd = "";
    }
  },
  created() {
    this.$http.get("/home/getHomeInfo").then(
      data => {
        console.info(data);
        if (data.body.code == 0) {
          //初始化主页数据
          this.avatar = data.body.data.userInfo.avatar;
          this.userName = data.body.data.userInfo.userName;
          this.schemeList = data.body.data.schemeList;
        } else {
          //初始化数据失败
          this.$router.push("/");
        }
      },
      error => {
        console.info(error);
      }
    );
  }
};
</script>