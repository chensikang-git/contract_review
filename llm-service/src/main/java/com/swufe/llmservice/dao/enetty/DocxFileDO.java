package com.swufe.llmservice.dao.enetty;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Data
@TableName("file") // 对应数据库的 user 表
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocxFileDO extends BaseDO {
    @TableId(type = IdType.AUTO) // 指定自增主键策略
    private Long id;
    private String fileName;

    private String filePath;

    private String fileExecutedPath;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date uploadTime;

    private Long userId;

    private Integer status;

    private FileStatusDO fileStatus; // 第二次查询时插入
}
