package com.tonkia.v3dmodel.pojo;

public class ResponseData {
    //code编码
    public static int SUCCEED=0;
    public static int FAILURE=1;
    public static int FAILURE_UNLOGIN=2;

    //返回格式
    private int code;
    private String msg;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResponseData(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
