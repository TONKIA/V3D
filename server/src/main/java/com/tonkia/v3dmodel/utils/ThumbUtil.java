package com.tonkia.v3dmodel.utils;

import net.coobird.thumbnailator.Thumbnails;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ThumbUtil {
    public static void thumb(InputStream is, OutputStream os, int size) {
        try {
            Thumbnails.of(is).size(size, size).outputQuality(0.5).toOutputStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
