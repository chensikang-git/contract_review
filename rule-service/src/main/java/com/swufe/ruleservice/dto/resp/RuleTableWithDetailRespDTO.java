package com.swufe.ruleservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RuleTableWithDetailRespDTO {
    private Long id;

    private String name;

    private String description;

    private Integer createdSource;

    private Long userId; // 非常重要的字段

    private List<RulesDetailRecordRespDTO> rulesDetailRecords;
}
