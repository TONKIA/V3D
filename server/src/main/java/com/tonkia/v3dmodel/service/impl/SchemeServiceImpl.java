package com.tonkia.v3dmodel.service.impl;

import com.tonkia.v3dmodel.mapper.SchemeMapper;
import com.tonkia.v3dmodel.pojo.SchemeInfo;
import com.tonkia.v3dmodel.pojo.UserInfo;
import com.tonkia.v3dmodel.service.SchemeService;
import com.tonkia.v3dmodel.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SchemeServiceImpl implements SchemeService {

    @Autowired
    SchemeMapper schemeMapper;
    @Value("${shareLink.prefix}")
    String shareLinkPrefix;

    @Override
    public String insertScheme(SchemeInfo schemeInfo, UserInfo userInfo) {
        String sid = UUIDUtil.generate();
        schemeInfo.setSid(sid);
        schemeInfo.setCreateTime(new Date());
        schemeInfo.setUid(userInfo.getUid());
        return schemeMapper.insertScheme(schemeInfo) ? sid : null;
    }

    @Override
    public List<SchemeInfo> getHomeSchemeList(Integer uid) {
        List<SchemeInfo> list = schemeMapper.getHomeSchemeList(uid);
        for (SchemeInfo schemeInfo : list) {
            schemeInfo.setShareLink(shareLinkPrefix + schemeInfo.getSid());
        }
        return list;
    }

    @Override
    public boolean hasPermission(String sid, UserInfo userInfo) {
        Map para = new HashMap<>();
        para.put("uid", userInfo.getUid());
        para.put("sid", sid);
        return schemeMapper.hasPermission(para) > 0;
    }

    @Override
    public boolean updateScheme(SchemeInfo schemeInfo) {
        return schemeMapper.updateScheme(schemeInfo);
    }

    @Override
    public SchemeInfo getSchemeInfo(Integer uid, String sid) {
        Map para = new HashMap<>();
        para.put("uid", uid);
        para.put("sid", sid);
        SchemeInfo schemeInfo = schemeMapper.getSchemeInfo(para);
        if (schemeInfo != null)
            schemeInfo.setShareLink(shareLinkPrefix + sid);
        return schemeInfo;
    }

    @Override
    public boolean updateShareInfo(SchemeInfo schemeInfo) {
        return schemeMapper.updateShareInfo(schemeInfo);
    }

    @Override
    public Integer getShareBySid(String sid) {
        return schemeMapper.getShareBySid(sid);
    }

    @Override
    public SchemeInfo getSchemeInfo(String sid) {
        return schemeMapper.getSchemeInfoBySid(sid);
    }

    @Override
    public boolean getCustomer(String sid, String password) {
        Map para = new HashMap<>();
        para.put("password", password);
        para.put("sid", sid);
        return schemeMapper.getCustomer(para)>0;
    }

    @Override
    public boolean deleteScheme(Integer uid, String sid) {
        Map para = new HashMap<>();
        para.put("uid", uid);
        para.put("sid", sid);
        return schemeMapper.deleteScheme(para);
    }


}
