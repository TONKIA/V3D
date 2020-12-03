package com.tonkia.v3dmodel.service;

import com.tonkia.v3dmodel.pojo.SchemeInfo;
import com.tonkia.v3dmodel.pojo.UserInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SchemeService {
    String insertScheme(SchemeInfo schemeInfo, UserInfo userInfo);

    List<SchemeInfo> getHomeSchemeList(Integer uid);

    boolean hasPermission(String sid, UserInfo userInfo);

    boolean updateScheme(SchemeInfo schemeInfo);

    SchemeInfo getSchemeInfo(Integer uid, String sid);

    boolean updateShareInfo(SchemeInfo schemeInfo);

    Integer getShareBySid(String sid);

    SchemeInfo getSchemeInfo(String sid);

    boolean getCustomer(String sid, String password);

    boolean deleteScheme(Integer uid, String sid);
}
