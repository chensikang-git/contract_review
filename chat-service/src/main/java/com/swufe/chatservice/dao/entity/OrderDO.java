package com.swufe.chatservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("orders") // 对应数据库的 lawyer 表
public class OrderDO extends BaseDO {

    @TableId(type = IdType.INPUT) // 使用自增策略作为主键
    private String orderId;  // 订单ID

    private Long sessionId; // 会话ID

    private String openid; // 用户ID

    private String orderStatus; // 订单状态

    private Integer orderAmount; // 订单金额

}
