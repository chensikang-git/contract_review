package com.swufe.ruleservice.dto.req;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmallRuleReqDTO {

    @NotNull(message = "风险等级不能为空")
    @Min(value = 0, message = "风险等级必须大于或等于0")
    @Max(value = 2, message = "风险等级必须小于或等于2")
    private Integer riskLevel;

    @NotNull(message = "小规则名称不能为空")
    private String smallRuleName;

    @NotNull(message = "小规则内容不能为空")
    private String smallRuleDescription;

    private Integer createdSource;

    private Long ruleTableId;
}
