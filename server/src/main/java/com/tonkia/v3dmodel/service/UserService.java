package com.tonkia.v3dmodel.service;

import com.tonkia.v3dmodel.pojo.UserInfo;

public interface UserService {
    UserInfo getUserInfo(UserInfo userInfo);

    UserInfo getHomeUserInfo(Integer uid);

    boolean updateAvatar(Integer uid, String path);

    boolean updateInfo(Integer uid, String userName, String password);
}
