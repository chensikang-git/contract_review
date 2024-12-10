package com.swufe.ruleservice.service;

import com.swufe.ruleservice.dto.req.SmallRuleReqDTO;
import com.swufe.ruleservice.dto.req.UpdateSmallRuleReqDTO;
import jakarta.validation.Valid;
import org.apache.shardingsphere.distsql.parser.statement.ral.UpdatableGlobalRuleRALStatement;
import org.springframework.stereotype.Service;

@Service
public interface RuleService {

    //增加小规则
    void addSmallRule(@Valid SmallRuleReqDTO smallRuleReqDTO);

    //删除小规则
//    void deleteSmallRule(@Valid String name);


//    public void deleteSmallRuleById(Integer smallId);// 根据id删除

    //更新小规则
    void updateSmallRule(@Valid UpdateSmallRuleReqDTO updateSmallRuleReqDTO);
}
