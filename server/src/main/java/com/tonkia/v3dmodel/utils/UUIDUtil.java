package com.tonkia.v3dmodel.utils;

import java.util.UUID;

public class UUIDUtil {
    public static String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        System.out.println(generate());
    }
}
