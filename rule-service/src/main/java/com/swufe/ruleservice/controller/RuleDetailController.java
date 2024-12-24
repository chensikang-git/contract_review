package com.swufe.ruleservice.controller;

import com.swufe.chatlaw.Results;
import com.swufe.chatlaw.result.Result;
import com.swufe.ruleservice.dto.req.SmallRuleReqDTO;
import com.swufe.ruleservice.dto.req.UpdateSmallRuleReqDTO;
import com.swufe.ruleservice.dto.resp.SmallRuleDetailRespDTO;
import com.swufe.ruleservice.service.RuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RuleDetailController {

    private final RuleService ruleService;

    /**
     * 修改小规则时查找小规则详情
     */
    @GetMapping("/api/rule-service/search-small-rule")
    public Result<SmallRuleDetailRespDTO> getSmallRuleDetail(Long smallRuleId) {
        return Results.success(ruleService.getSmallRuleDetail(smallRuleId));
    }


    /**
     * 增加小规则
     */
    @PostMapping("/api/rule-service/add-small-rule")
    public Result<Void> addSmallRule(@Valid @RequestBody SmallRuleReqDTO smallRuleReqDTO) {
        ruleService.addSmallRule(smallRuleReqDTO);
        return Results.success();
    }

    /**
     * 删除小规则
     */
    @DeleteMapping("/api/rule-service/delete-small-rule")
    public Result<Void> DeleteSmallRule(Long smallRuleId) {
        ruleService.deleteSmallRule(smallRuleId);
        return Results.success();
    }


    /**
     * 修改小规则
     */
    @PutMapping("/api/rule-service/update-small-rule")
    public Result<Void> updateSmallRule(@Valid @RequestBody UpdateSmallRuleReqDTO updateSmallRuleReqDTO) {
        ruleService.updateSmallRule(updateSmallRuleReqDTO);
        return Results.success();
    }
}



