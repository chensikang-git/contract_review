package com.swufe.chatservice.dto.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRespDTO extends BaseDO {
    private Long messageId;               // 消息ID
    private Long sessionId;               // 会话ID
    private String senderType;            // 消息发送者类型 ('user', 'model')
    private String senderId;              // 发送者ID (user_id, lawyer_id 或 model_id)
    private String messageText;           // 消息内容
    private Integer alignment;           // 消息内容
}