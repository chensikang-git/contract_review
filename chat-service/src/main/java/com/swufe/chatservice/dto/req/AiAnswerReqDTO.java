package com.swufe.chatservice.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swufe.chatservice.remote.req.ChatRecordReqDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAnswerReqDTO {
    private ArrayList<ChatRecordReqDTO.Dialogue> dialogues;  // 修改为 ArrayList
    @JsonProperty("ten_questions")
    private String tenQuestions;
}
