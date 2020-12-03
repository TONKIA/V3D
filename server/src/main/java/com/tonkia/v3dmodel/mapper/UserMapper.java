package com.tonkia.v3dmodel.mapper;

import com.tonkia.v3dmodel.pojo.UserInfo;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

public interface UserMapper {
    @Select("select * from tb_user where phoneNumber = #{phoneNumber}")
    UserInfo getUserInfo(UserInfo userInfo);

    @Select("select userName, avatar from tb_user where uid = #{uid}")
    UserInfo getHomeUserInfo(Integer uid);

    @Update("update tb_user set avatar =#{avatar} where uid = #{uid} ")
    boolean updateAvatar(Map para);

    @Update("update tb_user set userName =#{userName} where uid = #{uid} ")
    boolean updateUserName(Map para);

    @Update("update tb_user set password =#{password} where uid = #{uid} ")
    boolean updatePassword(Map para);
}
