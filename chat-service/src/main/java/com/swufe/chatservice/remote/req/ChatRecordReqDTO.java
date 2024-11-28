package com.swufe.chatservice.remote.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRecordReqDTO {
    private ArrayList<Dialogue> dialogues;
    // 定义内部类Dialogue

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Dialogue {
        private String role;  // 角色，例如"client"
        private String content;  // 对话内容
    }


}
