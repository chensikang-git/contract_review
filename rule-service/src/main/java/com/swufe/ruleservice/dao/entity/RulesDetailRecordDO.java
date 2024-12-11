package com.swufe.ruleservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("rules_detail") // 对应数据库的 user 表
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmallRuleDO extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO) // 使用自增策略作为主键
    private Long id;

    private String name;

    private String description;

    private Integer createdSource;

    private Integer riskLevel;
}
