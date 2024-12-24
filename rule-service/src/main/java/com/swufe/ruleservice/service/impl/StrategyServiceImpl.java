package com.swufe.ruleservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.swufe.chatlaw.DistributedCache;
import com.swufe.chatlaw.core.UserContext;
import com.swufe.chatlaw.exception.ClientException;
import com.swufe.chatlaw.exception.ServiceException;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.ruleservice.dao.entity.rule.RelatedRuleRecordDO;
import com.swufe.ruleservice.dao.entity.rule.RuleTableRecordDO;
import com.swufe.ruleservice.dao.entity.rule.RulesDetailRecordDO;
import com.swufe.ruleservice.dao.entity.strategy.RelatedStrategyRecordDO;
import com.swufe.ruleservice.dao.entity.strategy.StrategyTableRecordDO;
import com.swufe.ruleservice.dao.mapper.rule.RelatedRuleMapper;
import com.swufe.ruleservice.dao.mapper.rule.RuleTableMapper;
import com.swufe.ruleservice.dao.mapper.rule.RulesDetailMapper;
import com.swufe.ruleservice.dao.mapper.strategy.RelatedStrategyMapper;
import com.swufe.ruleservice.dao.mapper.strategy.StrategyMapper;
import com.swufe.ruleservice.dto.req.PageRequestExtend;
import com.swufe.ruleservice.dto.req.StrategyAddReqDTO;
import com.swufe.ruleservice.dto.req.StrategyUpdateReqDTO;
import com.swufe.ruleservice.dto.resp.RulesDetailRecordRespDTO;
import com.swufe.ruleservice.dto.resp.StrategyDetailRespDTO;
import com.swufe.ruleservice.dto.resp.StrategyRecordRespDTO;
import com.swufe.ruleservice.service.StrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static com.swufe.chatlaw.errorcode.BaseErrorCode.*;
import static com.swufe.ruleservice.common.constant.RulesConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyServiceImpl implements StrategyService {

    private final DistributedCache distributedCache;
    private final StrategyMapper strategyMapper;
    private final RulesDetailMapper rulesDetailMapper;
    private final RelatedRuleMapper relatedRuleMapper;
    private final RuleTableMapper ruleTableMapper;
    private final RelatedStrategyMapper relatedStrategyMapper;

    //根据小规则id去查关联表和大规则表并进行检验 返回小规则创建用户id
    public Long getRuleTableUserId(Long smallRuleId) {
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
        //根据大规则id去大规则表中得到创建者id
        return bigRule.getUserId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addStrategy(StrategyAddReqDTO strategyAddReqDTO) {
        Long userId = UserContext.getUserId();
        StrategyTableRecordDO strategyTableRecordDO = StrategyTableRecordDO.builder()
                .name(strategyAddReqDTO.getName())
                .userId(userId)
                .description(strategyAddReqDTO.getDescription())
                .build();
        //插入策略表
        int insert = strategyMapper.insert(strategyTableRecordDO);
        if (!SqlHelper.retBool(insert)) {
            throw new ServiceException(INSERT_ERROR);
        }
        // 使用HashSet去重
        Set<Long> uniqueSmallRuleIds = new HashSet<>(strategyAddReqDTO.getSmallRuleIds());

        //按照strategyAddReqDTO中的smallRuleIds 的id列表 去小规则表中查找小规则 并检查小规则创建id与当前用户id是否相同 相同则插入关联表
        for (Long smallRuleId : uniqueSmallRuleIds) {
            RulesDetailRecordDO rulesDetailRecordDO = distributedCache.get(
                    RULES_DETAIL_KEY + smallRuleId,
                    RulesDetailRecordDO.class,
                    () -> rulesDetailMapper.selectById(smallRuleId),
                    TIME_OUT_OF_SECONDS,
                    TimeUnit.SECONDS
            );
            //id对应小规则不存在
            Optional.ofNullable(rulesDetailRecordDO)
                    .orElseThrow(() -> new ClientException(SMALL_RULE_DISAPPEAR_ERROR));
            //检查小规则创建id与当前用户id是否相同
            Long ruleTableUserId = getRuleTableUserId(smallRuleId);
//            System.out.println("!!!!!!!!!!!!!!!rulesDetailRecordDO.getCreatedSource(): " + rulesDetailRecordDO.getCreatedSource());
            if (!Objects.equals(ruleTableUserId, userId) && Objects.equals(rulesDetailRecordDO.getCreatedSource(), 1)) {
                throw new ClientException(USER_UNAUTHORIZED_OPERATION_ERROR);
            }
            //插入关联表
            RelatedStrategyRecordDO relatedStrategyRecordDO = RelatedStrategyRecordDO.builder()
                    .strategyTableId(strategyTableRecordDO.getId())
                    .ruleDetailId(smallRuleId)
                    .build();
            int judge = relatedStrategyMapper.insert(relatedStrategyRecordDO);
            if (!SqlHelper.retBool(judge)) {
                throw new ServiceException(INSERT_ERROR);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStrategy(StrategyUpdateReqDTO strategyUpdateReqDTO) {
        //根据策略id查找策略  查找完验证是否存在 再验证用户是否是创建者 验证通过则更新策略表
        StrategyTableRecordDO strategyTableRecordDO = distributedCache.get(
                STRATEGY_TABLE_KEY + strategyUpdateReqDTO.getId(),
                StrategyTableRecordDO.class,
                () -> strategyMapper.selectById(strategyUpdateReqDTO.getId()),
                TIME_OUT_OF_SECONDS,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(strategyTableRecordDO)
                .orElseThrow(() -> new ClientException(STRATEGY_DISAPPEAR_ERROR));

        if (!Objects.equals(strategyTableRecordDO.getUserId(), UserContext.getUserId())) {
            throw new ClientException(USER_UNAUTHORIZED_OPERATION_ERROR);
        }
//        System.out.println("!!!strategyUpdateReqDTO.getName()=" + strategyUpdateReqDTO.getName());
        //更新策略表
        strategyTableRecordDO = StrategyTableRecordDO.builder()
                .id(strategyUpdateReqDTO.getId())
                .name(strategyUpdateReqDTO.getName())
                .description(strategyUpdateReqDTO.getDescription())
                .build();
        int update = strategyMapper.updateById(strategyTableRecordDO);
        if (!SqlHelper.retBool(update)) {
            throw new ServiceException(INSERT_ERROR);
        }


        //用set 的 RemoveAll方法来更新策略关联表中的小规则id列表
        List<Long> smallRuleIds = strategyUpdateReqDTO.getSmallRuleIds();
        List<Long> relatedRuleIds = relatedStrategyMapper.selectList(
                        new LambdaQueryWrapper<RelatedStrategyRecordDO>().eq(RelatedStrategyRecordDO::getStrategyTableId, strategyUpdateReqDTO.getId())
                )
                .stream()
                .map(RelatedStrategyRecordDO::getRuleDetailId)
                .toList();
        // 将smallRuleIds和relatedRuleIds转换为Set
        Set<Long> smallRuleIdSet = new HashSet<>(smallRuleIds);
        Set<Long> relatedRuleIdSet = new HashSet<>(relatedRuleIds);

        // 找出需要删除的规则ID
        Set<Long> toRemove = new HashSet<>(relatedRuleIdSet);
        toRemove.removeAll(smallRuleIdSet);

        // 找出需要插入的规则ID
        Set<Long> toAdd = new HashSet<>(smallRuleIdSet);
        toAdd.removeAll(relatedRuleIdSet);

        // 删除策略关联表中该策略的所有需要删除的小规则id
        if (!toRemove.isEmpty()) {
            relatedStrategyMapper.delete(
                    new LambdaQueryWrapper<RelatedStrategyRecordDO>()
                            .eq(RelatedStrategyRecordDO::getStrategyTableId, strategyUpdateReqDTO.getId())
                            .in(RelatedStrategyRecordDO::getRuleDetailId, toRemove)
            );
        }

        // 插入所给的小规则id对应的小规则
        for (Long smallRuleId : toAdd) {
            RulesDetailRecordDO rulesDetailRecordDO = distributedCache.get(
                    RULES_DETAIL_KEY + smallRuleId,
                    RulesDetailRecordDO.class,
                    () -> rulesDetailMapper.selectById(smallRuleId),
                    TIME_OUT_OF_SECONDS,
                    TimeUnit.SECONDS
            );
            Optional.ofNullable(rulesDetailRecordDO)
                    .orElseThrow(() -> new ClientException(SMALL_RULE_DISAPPEAR_ERROR));

            Long ruleTableUserId = getRuleTableUserId(smallRuleId);
            if (!Objects.equals(ruleTableUserId, UserContext.getUserId()) && !Objects.equals(ruleTableUserId, null)) {
                throw new ClientException(USER_UNAUTHORIZED_OPERATION_ERROR);
            }
            RelatedStrategyRecordDO relatedStrategyRecordDO = RelatedStrategyRecordDO.builder()
                    .strategyTableId(strategyUpdateReqDTO.getId())
                    .ruleDetailId(smallRuleId)
                    .build();
            int judge = relatedStrategyMapper.insert(relatedStrategyRecordDO);
            if (!SqlHelper.retBool(judge)) {
                throw new ServiceException(INSERT_ERROR);
            }
            distributedCache.delete(STRATEGY_TABLE_KEY + strategyUpdateReqDTO.getId());
        }
    }


    @Override
    public void deleteStrategy(Long id) {
        //id空值判断
        Optional.ofNullable(id)
                .orElseThrow(() -> new ClientException(SMALL_RULE_ID_EMPTY_ERROR));
        //要分别删除id对应的策略表和策略关联表的记录 中途需要验证用户是否是创建者
        StrategyTableRecordDO strategyTableRecordDO = distributedCache.get(
                STRATEGY_TABLE_KEY + id,
                StrategyTableRecordDO.class,
                () -> strategyMapper.selectById(id),
                TIME_OUT_OF_SECONDS,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(strategyTableRecordDO)
                .orElseThrow(() -> new ClientException(STRATEGY_DISAPPEAR_ERROR));
        //检验策略的创建id是否与当前用户id相同
        if (!Objects.equals(strategyTableRecordDO.getUserId(), UserContext.getUserId())) {
            throw new ClientException(USER_UNAUTHORIZED_OPERATION_ERROR);
        }

        int delete1 = strategyMapper.deleteById(id);
        if (!SqlHelper.retBool(delete1)) {
            throw new ServiceException(DELETE_ERROR);
        }
        int delete2 = relatedStrategyMapper.delete(
                new LambdaQueryWrapper<RelatedStrategyRecordDO>().eq(RelatedStrategyRecordDO::getStrategyTableId, id)
        );
        if (!SqlHelper.retBool(delete2)) {
            throw new ServiceException(DELETE_ERROR);
        }
        distributedCache.delete(STRATEGY_TABLE_KEY + id);


    }

    @Override
    public StrategyDetailRespDTO getStrategyDetail(Long id) {
        //id空值判断
        Optional.ofNullable(id)
                .orElseThrow(() -> new ClientException(SMALL_RULE_ID_EMPTY_ERROR));

        //先根据id得到策略DO
        StrategyTableRecordDO strategyTableRecordDO = distributedCache.get(
                STRATEGY_TABLE_KEY + id,
                StrategyTableRecordDO.class,
                () -> strategyMapper.selectById(id),
                TIME_OUT_OF_SECONDS,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(strategyTableRecordDO).orElseThrow(() -> new ServiceException(STRATEGY_DISAPPEAR_ERROR));
        //检验创建id与登录id
        if (!Objects.equals(strategyTableRecordDO.getUserId(), UserContext.getUserId())) {
            throw new ClientException(USER_UNAUTHORIZED_OPERATION_ERROR);
        }

        StrategyDetailRespDTO strategyDetailRespDTO = StrategyDetailRespDTO.builder()
                .name(strategyTableRecordDO.getName())
                .description(strategyTableRecordDO.getDescription())
                .build();

        //根据id 查策略关联表得到策略关联表中的小规则列表 并将每个小规则放入缓存
        List<RulesDetailRecordRespDTO> collect = relatedStrategyMapper.selectList(
                        new LambdaQueryWrapper<RelatedStrategyRecordDO>().eq(RelatedStrategyRecordDO::getStrategyTableId, id))
                .stream()
                .map(relatedStrategyRecordDO -> distributedCache.get(
                        RULES_DETAIL_KEY + relatedStrategyRecordDO.getRuleDetailId(),
                        RulesDetailRecordDO.class,
                        () -> rulesDetailMapper.selectOne(
                                new LambdaQueryWrapper<RulesDetailRecordDO>()
                                        .eq(RulesDetailRecordDO::getId, relatedStrategyRecordDO.getRuleDetailId())
                        ),
                        TIME_OUT_OF_SECONDS,
                        TimeUnit.SECONDS
                ))
                .map(rulesDetailRecordDO -> {
                    RulesDetailRecordRespDTO rulesDetailRecordRespDTO = new RulesDetailRecordRespDTO();
                    BeanUtils.copyProperties(rulesDetailRecordDO, rulesDetailRecordRespDTO);
                    return rulesDetailRecordRespDTO;
                })
                .toList();

        strategyDetailRespDTO.setRulesDetailRecords(collect);

        return strategyDetailRespDTO;
    }

    @Override
    public PageResponse<StrategyRecordRespDTO> getStrategiesByPage(PageRequestExtend pageRequestExtend) {
        IPage<StrategyTableRecordDO> page = new Page<>(pageRequestExtend.getCurrent(), pageRequestExtend.getSize());
        LambdaQueryWrapper<StrategyTableRecordDO> queryWrapper = new LambdaQueryWrapper<>();
        // 如果有模糊查询条件
        if (pageRequestExtend.getWords() != null && !pageRequestExtend.getWords().isEmpty()) {
            queryWrapper.like(StrategyTableRecordDO::getName, pageRequestExtend.getWords());
        }
        

        // 查询当前用户的策略
        queryWrapper.eq(StrategyTableRecordDO::getUserId, UserContext.getUserId());
        IPage<StrategyTableRecordDO> resultPage = strategyMapper.selectPage(page, queryWrapper);
        List<StrategyRecordRespDTO> convertedRecords = resultPage.getRecords()
                .stream()
                .map(
                        strategyTableRecordDO -> StrategyRecordRespDTO.builder()
                                .id(strategyTableRecordDO.getId())
                                .name(strategyTableRecordDO.getName())
                                .description(strategyTableRecordDO.getDescription())
                                .build()
                )
                .toList();

        return new PageResponse<>(resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getTotal(),
                convertedRecords
        );
    }


}