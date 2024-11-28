package com.swufe.chatservice.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequestDTO {
    private Long sessionId;
    private String userId;
    private String lawyerId;
    private String sessionType;
    private String Status;
}
