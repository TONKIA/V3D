package com.tonkia.v3dmodel.service.impl;

import com.tonkia.v3dmodel.mapper.FileMapper;
import com.tonkia.v3dmodel.mapper.SchemeMapper;
import com.tonkia.v3dmodel.pojo.CustomerInfo;
import com.tonkia.v3dmodel.pojo.FileInfo;
import com.tonkia.v3dmodel.pojo.FileRely;
import com.tonkia.v3dmodel.pojo.UserInfo;
import com.tonkia.v3dmodel.service.FileService;
import com.tonkia.v3dmodel.utils.FtpUtil;
import com.tonkia.v3dmodel.utils.ThumbUtil;
import com.tonkia.v3dmodel.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    FileMapper fileMapper;
    @Autowired
    SchemeMapper schemeMapper;

    @Value("${ftpclient.host}")
    private String host;
    @Value("${ftpclient.port}")
    private int port;
    @Value("${ftpclient.username}")
    private String username;
    @Value("${ftpclient.password}")
    private String password;
    @Value("${ftpclient.basePath}")
    private String basePath;
    @Value("${ftpclient.filePath}")
    private String filePath;
    @Value("${ftpclient.downloadPathPrefix}")
    private String downloadPathPrefix;

    @Override
    public List<FileRely> insertModelFile(UserInfo userInfo, String sid, MultipartFile[] files) {
        int uid = userInfo.getUid();
        List<FileRely> list = new ArrayList<>();
        for (MultipartFile file : files) {
            //判断是否是 .obj 或者 .fbx
            String subfix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
            if (subfix.toLowerCase().equals(".obj") || subfix.toLowerCase().equals(".fbx")) {
                String fileName = UUIDUtil.generate() + subfix;
                boolean isSucceed = false;
                try {
                    isSucceed = FtpUtil.uploadFile(host, port, username, password, basePath, filePath + "/models/" + sid, fileName, file.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isSucceed) {
                    FileRely fr = new FileRely();
                    FileInfo fi = new FileInfo();
                    String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
                    String fid = UUIDUtil.generate();
                    String path = downloadPathPrefix + "/models/" + sid + "/" + fileName;
                    fi.setFid(fid);
                    fi.setUid(uid);
                    fi.setSid(sid);
                    fi.setName(name);
                    fi.setPath(path);
                    fi.setType(file.getContentType());
                    if (fileMapper.insertFile(fi)) {
                        fr.setFid(fid);
                        fr.setName(name);
                        fr.setPath(path);
                        list.add(fr);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<FileRely> insertTextureFile(UserInfo userInfo, String sid, MultipartFile[] files) {
        int uid = userInfo.getUid();
        List<FileRely> list = new ArrayList<>();
        for (MultipartFile file : files) {
            //判断是否是 .jpg 或者 .png
            String subfix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
            if (subfix.toLowerCase().equals(".jpg") || subfix.toLowerCase().equals(".png")) {
                String fileName = UUIDUtil.generate() + subfix;
                boolean isSucceed = false;
                try {
                    isSucceed = FtpUtil.uploadFile(host, port, username, password, basePath, filePath + "/textures/" + sid, fileName, file.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isSucceed) {
                    FileRely fr = new FileRely();
                    FileInfo fi = new FileInfo();
                    String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
                    String fid = UUIDUtil.generate();
                    String path = downloadPathPrefix + "/textures/" + sid + "/" + fileName;
                    fi.setFid(fid);
                    fi.setUid(uid);
                    fi.setSid(sid);
                    fi.setName(name);
                    fi.setPath(path);
                    fi.setType(file.getContentType());
                    if (fileMapper.insertFile(fi)) {
                        fr.setFid(fid);
                        fr.setName(name);
                        fr.setPath(path);
                        list.add(fr);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public String insertCoverFile(String sid, MultipartFile file) {

        String subfix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        String fileName = UUIDUtil.generate() + subfix;
        boolean isSucceed = false;
        try {
            isSucceed = FtpUtil.uploadFile(host, port, username, password, basePath, filePath + "/cover/" + sid, fileName, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isSucceed) {
            String path = downloadPathPrefix + "/cover/" + sid + "/" + fileName;
            return path;
        }
        return null;
    }

    @Override
    public String uploadAvatar(int uid, MultipartFile file) {
        String fileName = UUIDUtil.generate();
        boolean isSucceed = false;
        try {
            isSucceed = FtpUtil.uploadFile(host, port, username, password, basePath, filePath + "/avatar/" + uid, fileName, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isSucceed) {
            String path = "/file/avatar/" + fileName;
            return path;
        }
        return null;
    }

    @Override
    public void avatar(Integer uid, String fileName,  HttpServletResponse response) {
        FtpUtil.downloadFileWithProgress(host, port, username, password, basePath + filePath + "/avatar/" + uid, fileName, response);
    }


    @Override
    public void download(UserInfo userInfo, CustomerInfo customerInfo, String type, String sid, String fileName, HttpServletResponse response, Integer size) {
        boolean hasRight = false;
        int share = schemeMapper.getShareBySid(sid);
        if (share == 1) {
            hasRight = true;
        } else if (userInfo != null) {
            Map para = new HashMap<>();
            para.put("uid", userInfo.getUid());
            para.put("sid", sid);
            hasRight = schemeMapper.hasPermission(para) > 0;
        } else if (share == 2 && customerInfo != null && customerInfo.getSid().equals(sid)) {
            hasRight = customerInfo.getSid().equals(sid);
        }
        if (hasRight) {
            try {
                if (size == null)
                    FtpUtil.downloadFileWithProgress(host, port, username, password, basePath + filePath + "/" + type + "/" + sid, fileName, response);
                else {
                    //如果指定了size返回压缩缩略图
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    FtpUtil.downloadFile(host, port, username, password, basePath + filePath + "/" + type + "/" + sid, fileName, baos);
                    ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
                    ThumbUtil.thumb(swapStream, response.getOutputStream(), size);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
