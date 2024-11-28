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
@TableName("chat_sessions") // 对应数据库的 会话 表
public class ChatSessionDO extends BaseDO{
    @TableId(value = "session_id", type = IdType.AUTO) // 使用自增策略作为主键
    private Long sessionId;               // 会话ID
    private String userId;                // 用户ID
    private String lawyerId;                // 律师ID（可为空）
    private String sessionType;           // 会话类型（‘user-model’ 或 ‘user-lawyer’）
    private String status;                // 会话状态 ('active' 或 'ended')
    private boolean userAgree;                // 会话状态 ('active' 或 'ended')
    private boolean lawyerAgree;                // 会话状态 ('active' 或 'ended')
    private String tenQuestions;
    private String caseType;
}