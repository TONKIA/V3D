package com.tonkia.v3dmodel.pojo;

import java.util.Date;

public class CommentInfo {
    private String text;
    private Date time;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
