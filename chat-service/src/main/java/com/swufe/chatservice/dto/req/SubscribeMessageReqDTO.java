package com.swufe.chatservice.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeMessageReqDTO {
    private String templateId; // 所需下发的订阅模板id
    private String page; // 点击模板卡片后的跳转页面（可选）
    private String toUser; // 接收者（用户）的 openid
    private Map<String, Map<String, String>> data; // 模板内容，格式形如 { "key1": { "value": any }, "key2": { "value": any } }
    private String miniprogramState; // 跳转小程序类型：developer, trial, formal
    private String lang; // 查看语言类型，zh_CN, en_US, zh_HK, zh_TW
}
