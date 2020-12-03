package com.tonkia.v3dmodel.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream("d:/web.png");
        boolean res = FtpUtil.uploadFile("192.168.226.128", 21, "ftpuser", "123456", "/home/ftpuser", "/v3d", "target", is);
        System.out.println(res);
    }
}
