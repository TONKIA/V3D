package com.tonkia.v3dmodel.controller;

import com.tonkia.v3dmodel.pojo.*;
import com.tonkia.v3dmodel.service.CommentService;
import com.tonkia.v3dmodel.service.SchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("show")
public class ShowController {
    @Autowired
    SchemeService schemeService;
    @Autowired
    CommentService commentService;

    @RequestMapping("/{sid}")
    @ResponseBody
    public ResponseData getShareType(@PathVariable("sid") String sid) {
        Integer code = schemeService.getShareBySid(sid);
        if (code != null) {
            Map data = new HashMap<>();
            data.put("share", code);
            return new ResponseData(ResponseData.SUCCEED, "获取方案分享类型成功", data);
        }
        return new ResponseData(ResponseData.FAILURE, "获取方案分享类型失败", null);
    }

    @RequestMapping("customer")
    @ResponseBody
    public ResponseData customer(HttpServletRequest request, String sid, String password) {
        if (schemeService.getCustomer(sid, password)) {
            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setSid(sid);
            request.getSession().setAttribute("customerInfo", customerInfo);
            return new ResponseData(ResponseData.SUCCEED, "密码有效", null);
        }
        return new ResponseData(ResponseData.FAILURE, "验证无效", null);
    }

    @RequestMapping("getComment")
    @ResponseBody
    public ResponseData getComment(HttpServletRequest request, String sid) {
        int share = schemeService.getShareBySid(sid);
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        CustomerInfo customerInfo = (CustomerInfo) request.getSession().getAttribute("customerInfo");
        if (userInfo != null && schemeService.hasPermission(sid, userInfo)) {
            List<CommentInfo> list = commentService.getAllComment(sid);
            if (list != null) {
                return new ResponseData(ResponseData.SUCCEED, "方案评论获取成功", list);
            }
        }
        if (share == 1) {
            List<CommentInfo> list = commentService.getAllComment(sid);
            if (list != null) {
                return new ResponseData(ResponseData.SUCCEED, "方案评论获取成功", list);
            }
        } else if (share == 2 && customerInfo != null && customerInfo.getSid().equals(sid)) {
            List<CommentInfo> list = commentService.getAllComment(sid);
            if (list != null) {
                return new ResponseData(ResponseData.SUCCEED, "方案评论获取成功", list);
            }
        }
        return new ResponseData(ResponseData.FAILURE, "方案评论获取失败", null);
    }

    @RequestMapping("comment")
    @ResponseBody
    public ResponseData comment(HttpServletRequest request, String sid,String text) {
        int share = schemeService.getShareBySid(sid);
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        CustomerInfo customerInfo = (CustomerInfo) request.getSession().getAttribute("customerInfo");
        if (userInfo != null && schemeService.hasPermission(sid, userInfo)) {
            if (commentService.insertComment(sid,text)) {
                return new ResponseData(ResponseData.SUCCEED, "方案评论成功", null);
            }
        }
        if (share == 1) {
            if (commentService.insertComment(sid,text)) {
                return new ResponseData(ResponseData.SUCCEED, "方案评论成功", null);
            }
        } else if (share == 2 && customerInfo != null && customerInfo.getSid().equals(sid)) {
            if (commentService.insertComment(sid,text)) {
                return new ResponseData(ResponseData.SUCCEED, "方案评论成功", null);
            }
        }
        return new ResponseData(ResponseData.FAILURE, "方案评论失败", null);
    }
}
