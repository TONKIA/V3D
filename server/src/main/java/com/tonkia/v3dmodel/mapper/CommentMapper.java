package com.tonkia.v3dmodel.mapper;

import com.tonkia.v3dmodel.pojo.CommentInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface CommentMapper {
    @Select("select text,time from tb_comment where sid = #{sid} order by time desc")
    List<CommentInfo> getAllComment(String sid);

    @Insert("insert into tb_comment(text,sid) values (#{text},#{sid})")
    boolean insertComment(Map para);
}
