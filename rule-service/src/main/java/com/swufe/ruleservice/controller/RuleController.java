package com.swufe.ruleservice.controller;

import com.swufe.chatlaw.Results;
import com.swufe.chatlaw.result.Result;
import com.swufe.ruleservice.dto.req.SmallRuleReqDTO;
import com.swufe.ruleservice.dto.req.UpdateSmallRuleReqDTO;
import com.swufe.ruleservice.service.RuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    /**
     *  获取大规则列表
     */
//    @PostMapping("/api/rule-service/add-small-rule")
    public Result<Void> getBigRuleList(@Valid @RequestBody SmallRuleReqDTO smallRuleReqDTO) {
        ruleService.addSmallRule(smallRuleReqDTO);
        return Results.success();
    }

    /**
     *  增加大规则
     */
//    @PostMapping("/api/rule-service/add-small-rule")
    public Result<Void> addBigRule(@Valid @RequestBody SmallRuleReqDTO smallRuleReqDTO) {
        ruleService.addSmallRule(smallRuleReqDTO);
        return Results.success();
    }

    /**
     *  修改大规则
     */
//    @PostMapping("/api/rule-service/add-small-rule")
    public Result<Void> updateBigRule(@Valid @RequestBody SmallRuleReqDTO smallRuleReqDTO) {
        ruleService.addSmallRule(smallRuleReqDTO);
        return Results.success();
    }

    /**
     *  删除大规则
     */
//    @PostMapping("/api/rule-service/add-small-rule")
    public Result<Void> deleteBigRule(@Valid @RequestBody SmallRuleReqDTO smallRuleReqDTO) {
        ruleService.addSmallRule(smallRuleReqDTO);
        return Results.success();
    }

    /**
     *  获取大规则详情
     */
//    @PostMapping("/api/rule-service/add-small-rule")
    public Result<Void> getBigRuleDetail(@Valid @RequestBody SmallRuleReqDTO smallRuleReqDTO) {
        ruleService.addSmallRule(smallRuleReqDTO);
        return Results.success();
    }

    /**
     *  根据大规则查找所有小规则
     */
//    @PostMapping("/api/rule-service/add-small-rule")
    public Result<Void> searchBigRule(@Valid @RequestBody SmallRuleReqDTO smallRuleReqDTO) {
        ruleService.addSmallRule(smallRuleReqDTO);
        return Results.success();
    }

    /**
     *  查找小规则详情
     */
//    @PostMapping("/api/rule-service/add-small-rule")
    public Result<Void> searchSmallRule(@Valid @RequestBody SmallRuleReqDTO smallRuleReqDTO) {
        ruleService.addSmallRule(smallRuleReqDTO);
        return Results.success();
    }


    /**
     *  增加小规则
     */
        @PostMapping("/api/rule-service/add-small-rule")
    public Result<Void> addSmallRule(@Valid @RequestBody SmallRuleReqDTO smallRuleReqDTO) {
        ruleService.addSmallRule(smallRuleReqDTO);
        return Results.success();
    }

    /**
     *  删除小规则
     */
    @PostMapping("/api/rule-service/delete-small-rule")
    public Result<Void> DeleteSmallRule(Long id) {
        ruleService.deleteSmallRule(id);
        return Results.success();
    }


    /**
     *  修改小规则
     */
    @PostMapping("/api/rule-service/update-small-rule")
    public Result<Void> updateSmallRule(@Valid @RequestBody UpdateSmallRuleReqDTO updateSmallRuleReqDTO) {
        ruleService.updateSmallRule(updateSmallRuleReqDTO);
        return Results.success();
    }
}



