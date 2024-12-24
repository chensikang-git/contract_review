package com.swufe.ruleservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.swufe.chatlaw.DistributedCache;
import com.swufe.chatlaw.core.UserContext;
import com.swufe.chatlaw.exception.ClientException;
import com.swufe.chatlaw.exception.ServiceException;
import com.swufe.ruleservice.dao.entity.rule.RuleTableRecordDO;
import com.swufe.ruleservice.dao.entity.rule.RelatedRuleRecordDO;
import com.swufe.ruleservice.dao.entity.rule.RulesDetailRecordDO;
import com.swufe.ruleservice.dao.entity.strategy.RelatedStrategyRecordDO;
import com.swufe.ruleservice.dao.mapper.rule.RuleTableMapper;
import com.swufe.ruleservice.dao.mapper.rule.RelatedRuleMapper;
import com.swufe.ruleservice.dao.mapper.rule.RulesDetailMapper;
import com.swufe.ruleservice.dao.mapper.strategy.RelatedStrategyMapper;
import com.swufe.ruleservice.dto.req.SmallRuleReqDTO;
import com.swufe.ruleservice.dto.req.UpdateSmallRuleReqDTO;
import com.swufe.ruleservice.dto.resp.SmallRuleDetailRespDTO;
import com.swufe.ruleservice.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.swufe.chatlaw.errorcode.BaseErrorCode.*;
import static com.swufe.ruleservice.common.constant.RulesConstant.RULES_DETAIL_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final RulesDetailMapper rulesDetailMapper;
    private final DistributedCache distributedCache;
    private final RelatedRuleMapper relatedRuleMapper;
    private final RuleTableMapper ruleTableMapper;
    private final RelatedStrategyMapper relatedStrategyMapper;

    @Override
    public void addSmallRule(SmallRuleReqDTO smallRuleReqDTO) {
        //查看小规则对应的大规则有没有存在于大规则表中
        Long ruleTableId = smallRuleReqDTO.getRuleTableId();
        Optional.ofNullable(ruleTableMapper.selectById(ruleTableId))
                .orElseThrow(() -> new ClientException(RELATED_BIG_RULE_DISAPPEAR_ERROR));

        //检查登录id和大规则id是否一样
        Long userId = UserContext.getUserId();
        if (!Objects.equals(userId, ruleTableMapper.selectById(ruleTableId).getUserId())) {
            throw new ClientException(USER_UNAUTHORIZED_OPERATION_ERROR);
        }

        //安全检查完则插入小规则表
        RulesDetailRecordDO rulesDetailRecordDO = BeanUtil.toBean(smallRuleReqDTO, RulesDetailRecordDO.class);
        rulesDetailRecordDO.setCreatedSource(1);
//        System.out.println("!!!!!!!!!!!!!!!smallRuleDO: " + rulesDetailRecordDO);
        int insert = rulesDetailMapper.insert(rulesDetailRecordDO);
        if (!SqlHelper.retBool(insert)) {
            throw new ServiceException(INSERT_ERROR);
        }

        // 插入成功并且返回id
        Long ruleDetailId = rulesDetailRecordDO.getId(); // 获取自动生成的小规则ID
//        System.out.println("ruleTableId ID: " + ruleDetailId);

        //插入关联表
        RelatedRuleRecordDO relatedRule = RelatedRuleRecordDO.builder()
                .ruleTableId(ruleTableId)
                .ruleDetailId(ruleDetailId)
                .build();

        // 插入数据到数据库
        int judge = relatedRuleMapper.insert(relatedRule);
        if (!SqlHelper.retBool(judge)) {
            throw new ServiceException(INSERT_ERROR);
        }

    }

    @Override
    public void deleteSmallRule(Long smallRuleId) {
        //id空值判断
        Optional.ofNullable(smallRuleId)
                .orElseThrow(() -> new ClientException(SMALL_RULE_ID_EMPTY_ERROR));

        RulesDetailRecordDO rulesDetailRecordDO = distributedCache.get(
                RULES_DETAIL_KEY + smallRuleId,
                RulesDetailRecordDO.class,
                () -> rulesDetailMapper.selectOne(
                        new LambdaQueryWrapper<RulesDetailRecordDO>().eq(RulesDetailRecordDO::getId, smallRuleId)
                ),
                7200,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(rulesDetailRecordDO).
                ifPresentOrElse(
                        // 如果小规则存在
                        ruleDetailDO -> {
                            //检查数据是否安全
                            // 检查来源字段是否为1
                            if (ruleDetailDO.getCreatedSource() != 1) {
                                throw new ClientException(SYSTEM_RULE_REVISE_ERROR);
                            }
                            checkSecurity(smallRuleId);

                            // 数据安全 可以删除
                            relatedRuleMapper.delete(new LambdaQueryWrapper<RelatedRuleRecordDO>()
                                    .eq(RelatedRuleRecordDO::getRuleDetailId, ruleDetailDO.getId()));

                            deleteRelatedStrategyAndCacheBySmallRuleId(ruleDetailDO.getId());

                            // 删除数据库中的数据
                            if (rulesDetailRecordDO != null) {
                                rulesDetailMapper.deleteById(rulesDetailRecordDO.getId());
                            }

                        },
                        () -> {
                            // 如果小规则不存在，抛出异常
                            throw new ClientException(SMALL_RULE_DISAPPEAR_ERROR);
                        }
                );
    }

    @Override
    public void updateSmallRule(UpdateSmallRuleReqDTO updateSmallRuleReqDTO) {
        RulesDetailRecordDO existingRulesDetailRecordDO = distributedCache.get(
                RULES_DETAIL_KEY + updateSmallRuleReqDTO.getSmallRuleId(),
                RulesDetailRecordDO.class,
                () -> rulesDetailMapper.selectOne(
                        new LambdaQueryWrapper<RulesDetailRecordDO>().eq(RulesDetailRecordDO::getId, updateSmallRuleReqDTO.getSmallRuleId())
                ),
                7200,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(existingRulesDetailRecordDO)
                .orElseThrow(() -> new ClientException(SMALL_RULE_DISAPPEAR_ERROR));

        //更新小规则前 检查数据安全
        // 检查创建源
        if (existingRulesDetailRecordDO.getCreatedSource() != 1) {
            throw new ClientException(SYSTEM_RULE_REVISE_ERROR);
        }

        checkSecurity(existingRulesDetailRecordDO.getId());

        // 更新小规则信息
        existingRulesDetailRecordDO = RulesDetailRecordDO.builder()
                .id(updateSmallRuleReqDTO.getSmallRuleId())
                .riskLevel(updateSmallRuleReqDTO.getRiskLevel())
                .name(updateSmallRuleReqDTO.getSmallRuleName())
                .description(updateSmallRuleReqDTO.getSmallRuleDescription())
                .build();
//        System.out.println("!!!!!!!!!!!!!!!existingSmallRuleDO: " + existingSmallRuleDO);
        // 更新数据库中的小规则
        int updateResult = rulesDetailMapper.updateById(existingRulesDetailRecordDO);
        if (!SqlHelper.retBool(updateResult)) {
            throw new ServiceException(INSERT_ERROR);
        }

        // 删除缓存中的小规则信息
        distributedCache.delete(RULES_DETAIL_KEY + updateSmallRuleReqDTO.getSmallRuleId());
    }

    @Override
    public SmallRuleDetailRespDTO getSmallRuleDetail(Long smallRuleId) {
        //id空值判断
        Optional.ofNullable(smallRuleId)
                .orElseThrow(() -> new ClientException(SMALL_RULE_ID_EMPTY_ERROR));

        //查看关系表中的对应关系
        RelatedRuleRecordDO relatedRule = relatedRuleMapper.selectOne(
                new LambdaQueryWrapper<RelatedRuleRecordDO>().eq(RelatedRuleRecordDO::getRuleDetailId, smallRuleId)
        );
        Optional.ofNullable(relatedRule)
                .orElseThrow(() -> new ClientException(RELATION_DISAPPEAR_ERROR));
        // 得到大规则id
        Long ruleTableId = relatedRule.getRuleTableId();

        // 查看是否存在对应大规则
        RuleTableRecordDO bigRule = distributedCache.get(
                RULES_DETAIL_KEY + ruleTableId,
                RuleTableRecordDO.class,
                () -> ruleTableMapper.selectOne(
                        new LambdaQueryWrapper<RuleTableRecordDO>().eq(RuleTableRecordDO::getId, ruleTableId)
                ),
                7200,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(bigRule)
                .orElseThrow(() -> new ClientException(RELATED_BIG_RULE_DISAPPEAR_ERROR));

        RulesDetailRecordDO rulesDetailRecordDO = distributedCache.get(
                RULES_DETAIL_KEY + smallRuleId,
                RulesDetailRecordDO.class,
                () -> rulesDetailMapper.selectOne(
                        new LambdaQueryWrapper<RulesDetailRecordDO>().eq(RulesDetailRecordDO::getId, smallRuleId)
                ),
                7200,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(rulesDetailRecordDO)
                .orElseThrow(() -> new ClientException(SMALL_RULE_DISAPPEAR_ERROR));

        return SmallRuleDetailRespDTO.builder()
                .riskLevel(rulesDetailRecordDO.getRiskLevel())
                .name(rulesDetailRecordDO.getName())
                .description(rulesDetailRecordDO.getDescription())
                .build();

    }










    // 检查数据安全
    public void checkSecurity(Long smallRuleId) {
        try {
            //查看关系表中的对应关系
            RelatedRuleRecordDO relatedRule = relatedRuleMapper.selectOne(
                    new LambdaQueryWrapper<RelatedRuleRecordDO>().eq(RelatedRuleRecordDO::getRuleDetailId, smallRuleId)
            );
            Optional.ofNullable(relatedRule)
                    .orElseThrow(() -> new ClientException(RELATION_DISAPPEAR_ERROR));
            // 得到大规则id
            Long ruleTableId = relatedRule.getRuleTableId();

            // 查看是否存在对应大规则
            RuleTableRecordDO bigRule = distributedCache.get(
                    RULES_DETAIL_KEY + ruleTableId,
                    RuleTableRecordDO.class,
                    () -> ruleTableMapper.selectOne(
                            new LambdaQueryWrapper<RuleTableRecordDO>().eq(RuleTableRecordDO::getId, ruleTableId)
                    ),
                    7200,
                    TimeUnit.SECONDS
            );
            Optional.ofNullable(bigRule)
                    .orElseThrow(() -> new ClientException(RELATED_BIG_RULE_DISAPPEAR_ERROR));

            // 检查登录id和大规则id是否一样
            Long userId = UserContext.getUserId();
            if (!Objects.equals(userId, bigRule.getUserId())) {
                throw new ClientException(SYSTEM_RULE_REVISE_ERROR);
            }

            log.info("数据安全检查通过，smallRuleId: {}, ruleTableId: {}, userId: {}", smallRuleId, ruleTableId, userId);
        } catch (ClientException e) {
            log.error("数据安全检查失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("未知错误: {}", e.getMessage(), e);
            throw new ClientException("系统错误");
        }
    }

    //根据小规则id删除缓存和策略关联表中的小规则
    public void deleteRelatedStrategyAndCacheBySmallRuleId(Long smallRuleId) {
        distributedCache.delete(RULES_DETAIL_KEY + smallRuleId);
        relatedStrategyMapper.delete(
                new LambdaQueryWrapper<RelatedStrategyRecordDO>().eq(RelatedStrategyRecordDO::getRuleDetailId, smallRuleId)
        );
    }

}
