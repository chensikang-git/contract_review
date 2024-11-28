package com.swufe.chatservice.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseTypeRespDTO {
    @JsonProperty("case_type")
    private String caseType;  // 案件类型

    @JsonProperty("enough_information")
    private Boolean enoughInformation;  // 是否有足够信息
}
