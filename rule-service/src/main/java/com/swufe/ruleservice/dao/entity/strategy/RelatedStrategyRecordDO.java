package com.swufe.ruleservice.dao.entity.strategy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("related_strategy")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatedStrategyRecordDO extends BaseDO {
    @TableId(value = "id", type = IdType.AUTO) // 使用自增策略作为主键
    private Long id;
    private Long strategyTableId;
    private Long ruleDetailId;
}
