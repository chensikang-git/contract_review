package com.swufe.llmservice.dao.enetty;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("file") // 对应数据库的 file 表
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocxFileDO extends BaseDO {
    @TableId(type = IdType.AUTO) // 指定自增主键策略
    private Long id;

    private String fileName;

    private String filePath;

    private String fileExecutedPath;

    private Long userId;

    private Long status;

    @TableField(exist = false)
    private DocxFileStatusDO fileStatus; // 第二次查询时插入
}
