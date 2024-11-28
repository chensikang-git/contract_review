package com.swufe.chatservice.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEventDTO {
    private String id;
    private String createTime;
    private String resourceType;
    private String eventType;
    private Resource resource;
    private String summary;
    @Data
    // 内部类，用于表示resource字段
    public static class Resource {
        private String algorithm;
        private String ciphertext;
        private String nonce;
        private String associatedData;

    }}
