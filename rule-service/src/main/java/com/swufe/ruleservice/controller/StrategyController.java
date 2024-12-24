package com.swufe.ruleservice.controller;

import com.swufe.chatlaw.Results;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.chatlaw.result.Result;
import com.swufe.ruleservice.dto.req.*;
import com.swufe.ruleservice.dto.resp.RuleTableRecordRespDTO;
import com.swufe.ruleservice.dto.resp.SmallRuleDetailRespDTO;
import com.swufe.ruleservice.dto.resp.StrategyDetailRespDTO;
import com.swufe.ruleservice.dto.resp.StrategyRecordRespDTO;
import com.swufe.ruleservice.service.RuleService;
import com.swufe.ruleservice.service.StrategyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StrategyController {

    private final StrategyService strategyService;

    /**
     * 新增策略
     */
    @PostMapping("/api/rule-service/addStrategy")
    public Result<Void> addStrategy(@Valid @RequestBody StrategyAddReqDTO strategyAddReqDTO) {
        strategyService.addStrategy(strategyAddReqDTO);
        return Results.success();
    }

    /**
     * 更新策略
     */
    @PutMapping("/api/rule-service/updateStrategy")
    public Result<Void> updateStrategy(@Valid @RequestBody StrategyUpdateReqDTO strategyUpdateReqDTO) {
        strategyService.updateStrategy(strategyUpdateReqDTO);
        return Results.success();
    }

    /**
     * 删除策略
     */
    @DeleteMapping("/api/rule-service/deleteStrategy")
    public Result<Void> deleteStrategy(Long id) {
        strategyService.deleteStrategy(id);
        return Results.success();
    }

    /**
     * 获取策略详情
     */
    @GetMapping("/api/rule-service/getStrategyDetail")
    public Result<StrategyDetailRespDTO> getStrategyDetail(Long id) {
        return Results.success(strategyService.getStrategyDetail(id));
    }

    /**
     * 分页查询策略（配模糊查询）
     */
    @GetMapping("/api/rule-service/getStrategiesByPage")
    public Result<PageResponse<StrategyRecordRespDTO>> getStrategiesByPage(PageRequestExtend pageRequestExtend) {
        return Results.success(strategyService.getStrategiesByPage(pageRequestExtend));
    }

}



