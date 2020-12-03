package com.tonkia.v3dmodel.service;

import com.tonkia.v3dmodel.pojo.CommentInfo;

import java.util.List;

public interface CommentService {
    List<CommentInfo> getAllComment(String sid);

    boolean insertComment(String sid, String text);
}
