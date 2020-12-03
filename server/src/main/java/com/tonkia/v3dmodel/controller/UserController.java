package com.tonkia.v3dmodel.controller;

import com.tonkia.v3dmodel.pojo.ResponseData;
import com.tonkia.v3dmodel.pojo.UserInfo;
import com.tonkia.v3dmodel.service.FileService;
import com.tonkia.v3dmodel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    FileService fileService;

    //验证登录
    @RequestMapping("hasLogin")
    @ResponseBody
    public ResponseData hasLogin(HttpServletRequest request) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            return new ResponseData(ResponseData.SUCCEED, "登录成功", null);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }

    //登录请求
    @RequestMapping("login")
    @ResponseBody
    public ResponseData login(HttpServletRequest request, UserInfo userInfo) {
        UserInfo ui = userService.getUserInfo(userInfo);
        if (ui == null) {
            //用户不存在
            return new ResponseData(ResponseData.FAILURE, "用户不存在", null);
        }
        if (!ui.getPassword().equals(userInfo.getPassword())) {
            //密码错误
            return new ResponseData(ResponseData.FAILURE, "密码错误", null);
        }
        request.getSession().setAttribute("userInfo", ui);
        return new ResponseData(ResponseData.SUCCEED, "登录成功", null);
    }

    //用户注销
    @RequestMapping("logout")
    @ResponseBody
    public ResponseData logout(HttpServletRequest request) {
        request.getSession().setAttribute("userInfo", null);
        return new ResponseData(ResponseData.SUCCEED, "注销成功", null);
    }

    //修改头像
    @RequestMapping("avatar")
    @ResponseBody
    public ResponseData avatar(HttpServletRequest request, MultipartFile file) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            String path = fileService.uploadAvatar(userInfo.getUid(), file);
            if (userService.updateAvatar(userInfo.getUid(), path)) {
                Map data = new HashMap();
                data.put("avatar", path);
                return new ResponseData(ResponseData.SUCCEED, "头像修改成功", data);
            }
            return new ResponseData(ResponseData.FAILURE, "头像修改失败", null);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }

    //修改头像
    @RequestMapping("changeInfo")
    @ResponseBody
    public ResponseData changeInfo(HttpServletRequest request, String userName, String password) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            if (userService.updateInfo(userInfo.getUid(),userName,password)) {
                return new ResponseData(ResponseData.SUCCEED, "用户信息修改成功", null);
            }
            return new ResponseData(ResponseData.FAILURE, "用户信息修改失败", null);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }
}
