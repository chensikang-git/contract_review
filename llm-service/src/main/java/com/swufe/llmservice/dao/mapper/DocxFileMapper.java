package com.swufe.llmservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swufe.llmservice.dao.enetty.DocxFileDO;
import com.swufe.llmservice.dto.resp.DocxFileRespDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface DocxFileMapper extends BaseMapper<DocxFileDO> {
//    @Select("select f.*, fs.id as fs_id, fs.status_name, fs.status_desc " +
//            "from file f " +
//            "left join file_status fs on f.status = fs.id " +
//            "where f.id = #{id} and f.user_id = #{userId}")
//    @Results(id = "docxFileRM", value = {
//            @Result(column = "file_name", property = "fileName"),
//            @Result(column = "file_path", property = "filePath"),
//            @Result(column = "create_time", property = "createTime"),
//            @Result(column = "status_name", property = "fileStatus.statusName"),
//            @Result(column = "status_desc", property = "fileStatus.statusDesc")
//    })
//    DocxFileDO getDocxFile(@Param("id") Long id, @Param("userId") Long userId);


//    @Select("select f.*, fs.id as fs_id, fs.status_name, fs.status_desc " +
//            "from file f " +
//            "left join file_status fs on f.status = fs.id " +
//            "where f.user_id = #{userId}")
//    @Results(id = "docxFileRM", value = {
//            @Result(column = "file_name", property = "fileName"),
//            @Result(column = "file_path", property = "filePath"),
//            @Result(column = "create_time", property = "createTime"),
//            @Result(column = "status_name", property = "fileStatus.statusName"),
//            @Result(column = "status_desc", property = "fileStatus.statusDesc")
//    })
//    List<DocxFileDO> getDocxFiles(@Param("userId") Long userId);
}
