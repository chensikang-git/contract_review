package com.swufe.chatservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReportRespDTO {
    private Integer score; // 分数
    private String brief;
    private String userProfiles ; // 用户简介
    private String caseProfiles ; // 案件简介
    private String caseAnalysis ; // 案件分析
    private Boolean IsReport ; // 案件分析
}