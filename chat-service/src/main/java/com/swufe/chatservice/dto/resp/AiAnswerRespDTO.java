package com.swufe.chatservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AiAnswerRespDTO {
    private String status;
    private Integer part;
    private String message;
    private String end;


}
