package com.swufe.userservice.remote.dto;

import lombok.Data;

@Data
public class PhoneInfoRespDTO {

    private int errcode; // 错误码
    private String errmsg; // 错误消息
    private PhoneInfo phone_info; // 嵌套的 PhoneInfo 对象

    @Data
    public static class PhoneInfo {
        private String phoneNumber; // 手机号码
        private String purePhoneNumber; // 不带国家码的手机号码
        private int countryCode; // 国家码
        private Watermark watermark; // 嵌套的 Watermark 对象
    }

    @Data
    public static class Watermark {
        private long timestamp; // 时间戳
        private String appid; // 应用ID
    }
}