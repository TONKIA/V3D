package com.tonkia.v3dmodel.mapper;

import com.tonkia.v3dmodel.pojo.FileInfo;
import org.apache.ibatis.annotations.Insert;

public interface FileMapper {
    @Insert("insert into tb_file(fid,uid,sid,type,path,name) values(#{fid},#{uid},#{sid},#{type},#{path},#{name})")
    boolean insertFile(FileInfo fi);
}
