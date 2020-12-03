package com.tonkia.v3dmodel.service;

import com.tonkia.v3dmodel.pojo.CustomerInfo;
import com.tonkia.v3dmodel.pojo.FileInfo;
import com.tonkia.v3dmodel.pojo.FileRely;
import com.tonkia.v3dmodel.pojo.UserInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

public interface FileService {
    void download(UserInfo userInfo, CustomerInfo customerInfo, String type, String sid, String fileName, HttpServletResponse response, Integer size);

    List<FileRely> insertModelFile(UserInfo userInfo, String sid, MultipartFile[] files);

    List<FileRely> insertTextureFile(UserInfo userInfo, String sid, MultipartFile[] files);

    String insertCoverFile(String sid,MultipartFile file);

    String uploadAvatar(int uid ,MultipartFile file);

    void avatar(Integer uid, String fileName,  HttpServletResponse response);
}
