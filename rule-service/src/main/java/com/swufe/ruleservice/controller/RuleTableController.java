package com.swufe.ruleservice.controller;


import com.swufe.chatlaw.Results;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.chatlaw.result.Result;
import com.swufe.ruleservice.dto.req.PageRequestExtend;
import com.swufe.ruleservice.dto.req.RuleTableRecordAddDTO;
import com.swufe.ruleservice.dto.req.RuleTableRecordUpdateDTO;
import com.swufe.ruleservice.dto.resp.RuleTableRecordRespDTO;
import com.swufe.ruleservice.dto.resp.RuleTableWithDetailRespDTO;
import com.swufe.ruleservice.service.RuleTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.swufe.ruleservice.common.constant.RulesConstant.PROJECT_CONTEXT_PATH;

@RequestMapping(PROJECT_CONTEXT_PATH)
@RestController
@RequiredArgsConstructor
public class RuleTableController {
    private final RuleTableService ruleTableService;

    @GetMapping("/table/gets")
    public Result<PageResponse<RuleTableRecordRespDTO>> getRuleTablesByPage(PageRequestExtend pageRequestExtend) {
        return Results.success(ruleTableService.getRuleTablesByPage(pageRequestExtend));
    }

    @DeleteMapping("/table/del/{id}")
    public Result<Void> deleteRuleTableRecord(@PathVariable("id") Long id) {
        ruleTableService.deleteRuleTableRecord(id);
        return Results.success();
    }

    @PostMapping("/table/add")
    public Result<Void> addRuleTableRecord(@RequestBody @Valid RuleTableRecordAddDTO ruleTableRecordAddDTO) {
        ruleTableService.addRuleTableRecord(ruleTableRecordAddDTO);
        return Results.success();
    }

    @PutMapping("/table/update")
    public Result<Void> updateRuleTableRecord(@RequestBody @Valid RuleTableRecordUpdateDTO ruleTableRecordUpdateDTO) {
        ruleTableService.updateRuleTableRecord(ruleTableRecordUpdateDTO);
        return Results.success();
    }

    @GetMapping("/table/get/{id}")
    public Result<RuleTableWithDetailRespDTO> getRuleTableRecord(@PathVariable("id") Long id) {
        return Results.success(ruleTableService.getRulesDetailsByRuleTableRecord(id));
    }


}
