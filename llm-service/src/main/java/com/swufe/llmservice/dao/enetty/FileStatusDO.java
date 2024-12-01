package com.swufe.llmservice.dao.enetty;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@Data
@TableName("file_status") // 对应数据库的 user 表
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileStatusDO extends BaseDO {
    @TableId(type = IdType.AUTO) // 指定自增主键策略
    private Long id;
    private String statusName;
    private String statusDesc;
}
