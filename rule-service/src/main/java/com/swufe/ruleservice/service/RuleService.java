package com.swufe.ruleservice.service;

import com.swufe.ruleservice.dto.req.SmallRuleReqDTO;
import com.swufe.ruleservice.dto.req.UpdateSmallRuleReqDTO;
import com.swufe.ruleservice.dto.resp.SmallRuleDetailRespDTO;
import jakarta.validation.Valid;
import org.apache.shardingsphere.distsql.parser.statement.ral.UpdatableGlobalRuleRALStatement;
import org.springframework.stereotype.Service;

@Service
public interface RuleService {

    //增加小规则
    void addSmallRule(@Valid SmallRuleReqDTO smallRuleReqDTO);

    //删除小规则
    void deleteSmallRule(@Valid Long smallRuleId);

    //更新小规则
    void updateSmallRule(@Valid UpdateSmallRuleReqDTO updateSmallRuleReqDTO);

    //修改小规则时查找小规则详情
    SmallRuleDetailRespDTO getSmallRuleDetail(Long smallRuleId);
}
