package com.tonkia.v3dmodel.service.impl;

import com.tonkia.v3dmodel.mapper.UserMapper;
import com.tonkia.v3dmodel.pojo.UserInfo;
import com.tonkia.v3dmodel.service.UserService;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public UserInfo getUserInfo(UserInfo userInfo) {
        return userMapper.getUserInfo(userInfo);
    }


    /**
     * 获取主页的用户信息，关键信息需要保留
     *
     * @param uid
     * @return
     */
    @Override
    public UserInfo getHomeUserInfo(Integer uid) {
        return userMapper.getHomeUserInfo(uid);
    }

    @Override
    public boolean updateAvatar(Integer uid, String path) {
        Map para = new HashMap();
        para.put("uid", uid);
        para.put("avatar", path);
        return userMapper.updateAvatar(para);
    }

    @Override
    public boolean updateInfo(Integer uid, String userName, String password) {
        boolean res = false;
        if (!TextUtils.isEmpty(userName)) {
            Map para = new HashMap();
            para.put("uid", uid);
            para.put("userName", userName);
            res = res || userMapper.updateUserName(para);
        }

        if (!TextUtils.isEmpty(password)) {
            Map para = new HashMap();
            para.put("uid", uid);
            para.put("password", password);
            res = res || userMapper.updatePassword(para);
        }
        return res;
    }
}
