package com.swufe.ruleservice.dto.req;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrategyAddReqDTO {


    @NotNull(message = "策略名称不能为空")
    private String name;

    @NotNull(message = "策略描述不能为空")
    private String description;

    @NotNull(message = "小规则id列表不能为空")
    private ArrayList<Long> smallRuleIds;
}
