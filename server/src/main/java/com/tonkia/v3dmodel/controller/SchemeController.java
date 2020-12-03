package com.tonkia.v3dmodel.controller;

import com.tonkia.v3dmodel.pojo.CustomerInfo;
import com.tonkia.v3dmodel.pojo.ResponseData;
import com.tonkia.v3dmodel.pojo.SchemeInfo;
import com.tonkia.v3dmodel.pojo.UserInfo;
import com.tonkia.v3dmodel.service.FileService;
import com.tonkia.v3dmodel.service.SchemeService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("scheme")
public class SchemeController {
    @Autowired
    SchemeService schemeService;
    @Autowired
    FileService fileService;

    /**
     * 创建一个新的方案
     *
     * @param request
     * @param name
     * @return
     */
    @RequestMapping("create")
    @ResponseBody
    public ResponseData create(HttpServletRequest request, String name) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        //对于插入操作为了安全，不能把对象暴露给前端啊，是不是？自己再封装一层，只插入name字段
        SchemeInfo schemeInfo = new SchemeInfo();
        schemeInfo.setName(name);
        if (userInfo != null) {
            if (!TextUtils.isEmpty(schemeInfo.getName())) {
                String sid = schemeService.insertScheme(schemeInfo, userInfo);
                if (sid != null) {
                    Map data = new HashMap<>();
                    data.put("sid", sid);
                    return new ResponseData(ResponseData.SUCCEED, "创建方案成功", data);
                }
                return new ResponseData(ResponseData.FAILURE, "新建方案失败", null);
            }
            return new ResponseData(ResponseData.FAILURE, "方案名称不能为空", null);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }

    /**
     * 保存方案
     *
     * @param request
     * @param schemeInfo
     * @param file
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResponseData save(HttpServletRequest request, SchemeInfo schemeInfo, MultipartFile file) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            if (!TextUtils.isEmpty(schemeInfo.getName())) {
                String cover = fileService.insertCoverFile(schemeInfo.getSid(), file);
                if (cover != null) {
                    schemeInfo.setCover(cover);
                    schemeInfo.setUid(userInfo.getUid());
                    if (schemeService.updateScheme(schemeInfo)) {
                        return new ResponseData(ResponseData.SUCCEED, "保存方案成功", null);
                    }
                    return new ResponseData(ResponseData.FAILURE, "保存方案失败", null);
                }
                return new ResponseData(ResponseData.FAILURE, "缩略图上传失败", null);
            }
            return new ResponseData(ResponseData.FAILURE, "方案名称不能为空", null);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }

    /**
     * 获取方案
     *
     * @param request
     * @param sid
     * @return
     */
    @RequestMapping("/{sid}")
    @ResponseBody
    public ResponseData getSchemeInfo(HttpServletRequest request, @PathVariable("sid") String sid) {
        int share = schemeService.getShareBySid(sid);
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        CustomerInfo customerInfo = (CustomerInfo) request.getSession().getAttribute("customerInfo");
        if (userInfo != null) {
            SchemeInfo schemeInfo = schemeService.getSchemeInfo(userInfo.getUid(), sid);
            if (schemeInfo != null) {
                return new ResponseData(ResponseData.SUCCEED, "方案数据获取成功", schemeInfo);
            }
        }
        if (share == 1) {
            SchemeInfo schemeInfo = schemeService.getSchemeInfo(sid);
            if (schemeInfo != null) {
                return new ResponseData(ResponseData.SUCCEED, "方案数据获取成功", schemeInfo);
            }
        } else if (share == 2 && customerInfo != null && customerInfo.getSid().equals(sid)) {
            SchemeInfo schemeInfo = schemeService.getSchemeInfo(sid);
            if (schemeInfo != null) {
                return new ResponseData(ResponseData.SUCCEED, "方案数据获取成功", schemeInfo);
            }
        }
        return new ResponseData(ResponseData.FAILURE, "方案数据获取失败", null);
    }

    @RequestMapping("/share")
    @ResponseBody
    public ResponseData share(HttpServletRequest request, SchemeInfo schemeInfo) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            schemeInfo.setUid(userInfo.getUid());
            if (schemeService.updateShareInfo(schemeInfo)) {
                return new ResponseData(ResponseData.SUCCEED, "更新分享数据成功", null);
            }
            return new ResponseData(ResponseData.FAILURE, "更新分享数据失败", null);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, String sid) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            if (schemeService.deleteScheme(userInfo.getUid(), sid)) {
                return new ResponseData(ResponseData.SUCCEED, "删除方案成功", null);
            }
            return new ResponseData(ResponseData.FAILURE, "删除方案失败", null);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }

}
