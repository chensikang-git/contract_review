package com.swufe.chatservice.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeChatPayReqDTO {

    private String mchid;
    @JsonProperty("out_trade_no")// 商户ID
    private String outTradeNo;     // 商户订单号
    private String appid;          // 应用ID
    private String description;    // 商品描述
    @JsonProperty("notify_url")// 商户ID
    private String notifyUrl;      // 回调通知URL
    private Amount amount;         // 金额信息
    private Payer payer;           // 支付者信息

    @Data
    @Builder
    public static class Amount {
        private int total;         // 总金额
        private String currency;   // 货币类型
    }

    @Data
    @Builder
    public static class Payer {
        private String openid;     // 用户的唯一标识
    }
}
