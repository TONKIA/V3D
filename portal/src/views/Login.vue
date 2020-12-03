<template>
  <v-app dark>
    <v-content>
      <v-alert
        dismissible
        v-model="alert"
        type="error"
        transition="scale-transition"
        style="position:absolute;width:100%;margin:0px"
      >{{msg}}</v-alert>
      <v-container fluid fill-height>
        <v-layout align-center justify-center>
          <v-flex sm12 md4>
            <v-card light class="elevation-12">
              <v-toolbar dark color="red">
                <v-toolbar-title>用户登录</v-toolbar-title>
                <v-spacer></v-spacer>
                <!-- 提示的方向 -->
                <v-tooltip bottom>
                  <!-- 这是需要提示的部件，任意 -->
                  <!-- on 是 v-tooltip 传递过来的事件处理方法 -->
                  <template v-slot:activator="{on}">
                    <v-btn icon large v-on="on">
                      <v-icon large>info</v-icon>
                    </v-btn>
                  </template>
                  <!-- 这是默认插槽，即提示的内容 -->
                  <span>关于V3D</span>
                </v-tooltip>
              </v-toolbar>
              <v-card-text>
                <v-form>
                  <v-text-field
                    prepend-icon="phone"
                    name="phoneNumber"
                    label="手机号"
                    type="tel"
                    color="red"
                    v-model="phoneNumber"
                    mask="###########"
                  />
                  <v-text-field
                    prepend-icon="lock"
                    name="password"
                    label="密码"
                    type="password"
                    color="red"
                    v-model="password"
                  />
                </v-form>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn dark color="red" @click="login">登 录</v-btn>
              </v-card-actions>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
export default {
  data: () => ({
    alert: false,
    msg: "",
    phoneNumber: "",
    password: ""
  }),
  methods: {
    login() {
      this.$http
        .post(
          "/user/login",
          {
            phoneNumber: this.phoneNumber,
            password: this.password
          },
          {}
        )
        .then(
          data => {
            console.info(data);
            if (data.body.code == 0) {
              this.$router.push("home");
            } else {
              this.alert = true;
              this.msg = data.body.msg;
            }
          },
          error => {
            console.info(error);
          }
        );
    }
  },
  beforeCreate() {
    this.$http.get("/user/hasLogin").then(
      data => {
        console.info(data);
        if (data.body.code == 0) {
          this.$router.push("home");
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
</style>
