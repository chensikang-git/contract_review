package com.swufe.chatservice.dto.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor  // 生成无参构造函数
@Builder
public class ChatSessionRespDTO  {
    private Long sessionId;               // 会话ID
    private String userId;
    private String lawyerId;                // 律师ID（可为空）
    private String sessionType;           // 会话类型（‘user-model’ 或 ‘user-lawyer’）
    private String status;                // 会话状态 ('active' 或 'ended')

    private Date createTime;
    private String caseType;
}