package com.swufe.chatservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swufe.chatlaw.base.BaseDO;
import com.swufe.chatservice.toolkit.FastJsonListTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("lawyer") // 对应数据库的 lawyer 表
public class LawyerDO extends BaseDO {

    /**
     * 律师 ID
     */
    @TableId(value = "lawyer_id", type = IdType.AUTO) // 使用自增策略作为主键
    private Long lawyerId;

    /**
     * 微信用户唯一标识
     */
    private String openid;

    /**
     * 所属律所
     */
    private String lawFirm;

    /**
     * 专业领域
     */
    @TableField(typeHandler = FastJsonListTypeHandler.class)
    private List<String> domain;

    /**
     * 性别
     */
    private String sex;
    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 毕业院校
     */
    private String graduateSchool;

    /**
     *  初次职业
     */
    private String firstOccupation;

    /**
     *  就职律所
     */
    private String workFirm;

    /**
     *  目前职位
     */
    private String presentPosition;

    /**
     * 职业执照
     */
    private String professionalPhoto;
    /**
     * 职业资格证编号
     */
    private String card;
    /**
     * 律师地址
     */
    private String address;

}