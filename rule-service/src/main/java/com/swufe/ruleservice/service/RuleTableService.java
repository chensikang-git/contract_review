package com.swufe.ruleservice.service;

import com.swufe.chatlaw.page.PageResponse;
import com.swufe.ruleservice.dto.req.*;
import com.swufe.ruleservice.dto.resp.RuleTableRecordRespDTO;
import com.swufe.ruleservice.dto.resp.RuleTableWithDetailRespDTO;
import com.swufe.ruleservice.dto.resp.RulesDetailRecordRespDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RuleTableService {

    //分页获取大规则，加模糊查找功能 done
    PageResponse<RuleTableRecordRespDTO> getRuleTablesByPage(PageRequestExtend pageRequestExtend);

    //删除一个大规则 half
    void deleteRuleTableRecord(Long ruleTableRecordId);

    //增加一个大规则 done
    void addRuleTableRecord(RuleTableRecordAddDTO ruleTableRecordAddDTO);

    //更新一个大规则 done
    void updateRuleTableRecord(RuleTableRecordUpdateDTO ruleTableRecordUpdateDTO);

    //获取一个大规则详情（包含大规则下的所有小规则） done
    RuleTableWithDetailRespDTO getRulesDetailsByRuleTableRecord(Long ruleTableRecordId);

    //通过大规则id获取所属的所有小规则 done
    List<RulesDetailRecordRespDTO> getRulesDetailsByRuleTableRecordId(Long ruleTableRecordId);



}
