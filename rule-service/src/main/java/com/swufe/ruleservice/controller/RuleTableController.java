package com.swufe.ruleservice.controller;


import com.swufe.ruleservice.service.RuleTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.swufe.ruleservice.common.constant.RulesConstant.PROJECT_CONTEXT_PATH;

@RequestMapping(PROJECT_CONTEXT_PATH)
@RestController
@RequiredArgsConstructor
public class RuleTableController {
    private final RuleTableService ruleTableService;


}
