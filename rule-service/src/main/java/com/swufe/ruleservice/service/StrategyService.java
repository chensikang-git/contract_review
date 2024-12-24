package com.swufe.ruleservice.service;

import com.swufe.chatlaw.page.PageResponse;
import com.swufe.ruleservice.dto.req.*;
import com.swufe.ruleservice.dto.resp.SmallRuleDetailRespDTO;
import com.swufe.ruleservice.dto.resp.StrategyDetailRespDTO;
import com.swufe.ruleservice.dto.resp.StrategyRecordRespDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface StrategyService {

    //增加策略
    void addStrategy(@Valid StrategyAddReqDTO strategyAddReqDTO);

    //更新策略
    void updateStrategy(@Valid StrategyUpdateReqDTO strategyUpdateReqDTO);

    //删除策略
    void deleteStrategy(Long id);

    //获取策略详情
    StrategyDetailRespDTO getStrategyDetail(Long id);

    //分页查询策略（配模糊查询）
    PageResponse<StrategyRecordRespDTO> getStrategiesByPage(PageRequestExtend pageRequestExtend);
}
