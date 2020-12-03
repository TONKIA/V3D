package com.tonkia.v3dmodel.service.impl;

import com.tonkia.v3dmodel.mapper.CommentMapper;
import com.tonkia.v3dmodel.pojo.CommentInfo;
import com.tonkia.v3dmodel.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentMapper commentMapper;

    @Override
    public List<CommentInfo> getAllComment(String sid) {
        return commentMapper.getAllComment(sid);
    }

    @Override
    public boolean insertComment(String sid, String text) {
        Map para = new HashMap<>();
        para.put("sid", sid);
        para.put("text", text);
        return commentMapper.insertComment(para);
    }
}
