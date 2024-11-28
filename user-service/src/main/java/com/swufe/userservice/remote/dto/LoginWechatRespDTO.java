package com.swufe.userservice.remote.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginWechatRespDTO {
    private int errcode; // 错误码
    private String errmsg; // 错误消息

    @JsonProperty("session_key")
    private String sessionKey;

    private String openid;
    private String unionid;
}