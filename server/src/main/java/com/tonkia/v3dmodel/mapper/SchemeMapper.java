package com.tonkia.v3dmodel.mapper;

import com.tonkia.v3dmodel.pojo.SchemeInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface SchemeMapper {
    @Insert("insert into tb_scheme(sid,uid,name,createTime) values(#{sid},#{uid},#{name},#{createTime})")
    boolean insertScheme(SchemeInfo schemeInfo);

    @Select("select sid,name,cover,share,password from tb_scheme where uid = #{0} and state = 0 order by createTime desc")
    List<SchemeInfo> getHomeSchemeList(Integer uid);

    @Select("select count(*) from tb_scheme where uid = #{uid} and sid = #{sid}")
    int hasPermission(Map para);

    @Update("update tb_scheme set name=#{name},cover=#{cover},data=#{data},camera=#{camera} where uid = #{uid} and sid = #{sid}")
    boolean updateScheme(SchemeInfo schemeInfo);

    @Select("select name,data,camera,share,password from tb_scheme where uid = #{uid} and sid = #{sid}")
    SchemeInfo getSchemeInfo(Map para);

    @Update("update tb_scheme set share = #{share} ,password = #{password} where uid = #{uid} and sid = #{sid}")
    boolean updateShareInfo(SchemeInfo schemeInfo);

    @Select("select share from tb_scheme where sid = #{sid}")
    Integer getShareBySid(String sid);

    @Select("select name,data,camera from tb_scheme where sid = #{sid}")
    SchemeInfo getSchemeInfoBySid(String sid);

    @Select("select count(*) from tb_scheme where sid = #{sid} and password = #{password} and share = 2")
    int getCustomer(Map para);

    @Delete("delete from tb_scheme where sid = #{sid} and uid = #{uid}")
    boolean deleteScheme(Map para);
}
