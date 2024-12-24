package com.swufe.ruleservice.dto.resp;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrategyDetailRespDTO {


    private String name;

    private String description;

    private List<RulesDetailRecordRespDTO> rulesDetailRecords;


}
