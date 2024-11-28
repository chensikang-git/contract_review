package com.swufe.chatservice.dto.req;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    @NotNull(message = "会话ID不能为空")
    private Long sessionId; // 会话ID

    @NotBlank(message = "发送者类型不能为空")
    @Pattern(regexp = "user|model|lawyer", message = "发送者类型只能是 'user' 或 'model'或‘lawyer")
    private String senderType; // 消息发送者类型 ('user', 'model')

    @Size(max = 100, message = "发送者ID长度不能超过100个字符") // 假设ID长度不应超过50
    private String senderId; // 发送者ID (user_id, lawyer_id 或 model_id)

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 1000, message = "消息内容长度不能超过1000个字符") // 假设消息长度不应超过500
    private String messageText; // 消息内容
}
