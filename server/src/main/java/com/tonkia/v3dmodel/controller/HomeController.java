package com.tonkia.v3dmodel.controller;

import com.tonkia.v3dmodel.pojo.ResponseData;

import com.tonkia.v3dmodel.pojo.SchemeInfo;
import com.tonkia.v3dmodel.pojo.UserInfo;
import com.tonkia.v3dmodel.service.SchemeService;
import com.tonkia.v3dmodel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("home")
public class HomeController {
    @Autowired
    UserService userService;
    @Autowired
    SchemeService schemeService;

    /**
     * 主页信息请求
     * @param request
     * @return
     */
    @RequestMapping("getHomeInfo")
    @ResponseBody
    public ResponseData getHomeInfo(HttpServletRequest request) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            UserInfo ui = userService.getHomeUserInfo(userInfo.getUid());
            List<SchemeInfo> schemeList = schemeService.getHomeSchemeList(userInfo.getUid());
            Map data = new HashMap<>();
            data.put("userInfo", ui);
            data.put("schemeList", schemeList);
            return new ResponseData(ResponseData.SUCCEED, "主页信息获取成功", data);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }

}
