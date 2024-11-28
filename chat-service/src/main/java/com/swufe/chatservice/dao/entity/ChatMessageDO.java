package com.swufe.chatservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("chat_messages") // 对应数据库的 会话 表
public class ChatMessageDO extends BaseDO {
    @TableId(value = "message_id", type = IdType.AUTO) // 使用自增策略作为主键
    private Long messageId;               // 消息ID
    private Long sessionId;               // 会话ID
    private String senderType;            // 消息发送者类型 ('user', 'model')
    private String senderId;              // 发送者ID (user_id, lawyer_id 或 model_id)
    private String messageText;           // 消息内容
}