package com.swufe.chatservice.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIAnswerStreamDTO {
    private String id;
    private String object;
    private Long created;
    private String model;
    private String system_fingerprint;
    private List<Choice> choices;

    // Getters and Setters
    @Data
    public static class Choice {
        private Integer index;
        private Delta delta;
        private Object logprobs;
        private Object finish_reason;

        // Getters and Setters
        @Data
        public static class Delta {
            private String content;

            // Getters and Setters
        }
    }
}
