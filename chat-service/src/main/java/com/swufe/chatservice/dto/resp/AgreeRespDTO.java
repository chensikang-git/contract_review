package com.swufe.chatservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgreeRespDTO {
    private boolean userAgree;
    private boolean lawyerAgree;
    private boolean lawyerPay;

}
