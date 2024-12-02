package com.swufe.llmservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swufe.llmservice.dao.enetty.DocxFileDO;
import org.apache.ibatis.annotations.*;


@Mapper
public interface DocxFileMapper extends BaseMapper<DocxFileDO> {

    @Select("select f.*, fs.id as fs_id, fs.status_name, fs.status_desc " +
            "from file f " +
            "left join file_status fs on f.status = fs.id " +
            "where f.id = #{id} and f.user_id = #{userId}")
    @Results(id = "docxFileRM", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "file_name", property = "fileName"),
            @Result(column = "file_path", property = "filePath"),
            @Result(column = "file_executed_path", property = "fileExecutedPath"),
            @Result(column = "status", property = "status"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "fs_id", property = "fileStatus.id"),
            @Result(column = "status_name", property = "fileStatus.statusName"),
            @Result(column = "status_desc", property = "fileStatus.statusDesc")
    })
    DocxFileDO getDocxFileByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
