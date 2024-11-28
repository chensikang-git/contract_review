package com.swufe.chatservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor  // 生成无参构造函数
@Builder
public class PreOrderRespDTO {
    private String appId;

    // 时间戳
    private String timeStamp;

    // 随机字符串
    private String nonceStr;

    // 订单详情扩展字符串
    private String packageStr;  // "package" 是 Java 关键字，所以使用 "packageStr" 代替

    // 签名方式
    private String signType;

    // 签名
    private String paySign;

}
