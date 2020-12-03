package com.tonkia.v3dmodel.controller;

import com.tonkia.v3dmodel.pojo.*;
import com.tonkia.v3dmodel.service.FileService;
import com.tonkia.v3dmodel.service.SchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("file")
public class FileController {

    @Autowired
    FileService fileService;
    @Autowired
    SchemeService schemeService;

    /**
     * 上传模型
     *
     * @param request
     * @param files
     * @param sid
     * @return
     */
    @RequestMapping("uploadModel")
    @ResponseBody
    public ResponseData uploadModel(HttpServletRequest request, MultipartFile[] files, String sid) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            if (schemeService.hasPermission(sid, userInfo)) {
                List<FileRely> fileInfoList = fileService.insertModelFile(userInfo, sid, files);
                Map data = new HashMap<>();
                data.put("uploadReply", fileInfoList);
                return new ResponseData(ResponseData.SUCCEED, "文件上传成功", data);
            }
            return new ResponseData(ResponseData.FAILURE, "该用户没有当前方案权限", null);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }

    /**
     * 上传贴图
     *
     * @param request
     * @param files
     * @param sid
     * @return
     */
    @RequestMapping("uploadTexture")
    @ResponseBody
    public ResponseData uploadTexture(HttpServletRequest request, MultipartFile[] files, String sid) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            if (schemeService.hasPermission(sid, userInfo)) {
                List<FileRely> fileInfoList = fileService.insertTextureFile(userInfo, sid, files);
                Map data = new HashMap<>();
                data.put("uploadReply", fileInfoList);
                return new ResponseData(ResponseData.SUCCEED, "文件上传成功", data);
            }
            return new ResponseData(ResponseData.FAILURE, "该用户没有当前方案权限", null);
        }
        return new ResponseData(ResponseData.FAILURE_UNLOGIN, "用户未登录", null);
    }

    /**
     * 资源请求
     *
     * @param request
     * @param response
     * @param type     资源类型
     * @param sid      所属方案
     * @param fileName 文件名
     * @param size     图片压缩的大小
     */
    //后缀名的匹配规则
    @RequestMapping("download/{type}/{sid}/{fileName:.+}")
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable("type") String type, @PathVariable("sid") String sid, @PathVariable("fileName") String fileName, Integer size) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        CustomerInfo customerInfo = (CustomerInfo) request.getSession().getAttribute("customerInfo");
        fileService.download(userInfo, customerInfo, type, sid, fileName, response, size);
    }

    @RequestMapping("avatar/{fileName:.+}")
    public void avatar(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            fileService.avatar(userInfo.getUid(), fileName,  response);
        }
    }
}
