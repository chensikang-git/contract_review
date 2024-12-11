package com.swufe.ruleservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.swufe.chatlaw.DistributedCache;
import com.swufe.chatlaw.core.UserContext;
import com.swufe.chatlaw.exception.ServiceException;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.ruleservice.dao.entity.RelatedRuleRecordDO;
import com.swufe.ruleservice.dao.entity.RuleTableRecordDO;
import com.swufe.ruleservice.dao.entity.RulesDetailRecordDO;
import com.swufe.ruleservice.dao.mapper.RelatedRuleMapper;
import com.swufe.ruleservice.dao.mapper.RuleTableMapper;
import com.swufe.ruleservice.dao.mapper.RulesDetailMapper;
import com.swufe.ruleservice.dto.req.*;
import com.swufe.ruleservice.dto.resp.RuleTableRecordRespDTO;
import com.swufe.ruleservice.dto.resp.RuleTableWithDetailRespDTO;
import com.swufe.ruleservice.dto.resp.RulesDetailRecordRespDTO;
import com.swufe.ruleservice.service.RuleTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.swufe.chatlaw.errorcode.BaseErrorCode.*;
import static com.swufe.ruleservice.common.constant.RulesConstant.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class RuleTableServiceImpl implements RuleTableService {

    private final DistributedCache distributedCache;
    private final RuleTableMapper ruleTableMapper;
    private final RulesDetailMapper rulesDetailMapper;
    private final RelatedRuleMapper relatedRuleMapper;


    // 校验与获取一并进行，用户只能拿到系统与自己的大规则记录
    private RuleTableRecordDO getLegalRuleTableRecord(Long ruleTableRecordId) {
        RuleTableRecordDO ruleTableRecordDO = distributedCache.get(
                RULE_TABLE_KEY + ruleTableRecordId,
                RuleTableRecordDO.class,
                () -> ruleTableMapper.selectOne(new LambdaQueryWrapper<RuleTableRecordDO>()
                        .eq(RuleTableRecordDO::getId, ruleTableRecordId)),
                TIME_OUT_OF_SECONDS,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(ruleTableRecordDO).orElseThrow(() -> new ServiceException(RULE_TABLE_NOT_EXIST_ERROR));

        return Optional.ofNullable(ruleTableRecordDO.getUserId())
                .filter(userId -> userId.equals(UserContext.getUserId()))
                .map(__ -> ruleTableRecordDO) // 如果条件满足，返回 ruleTableRecordDO
                .orElseGet(() -> {
                    if (ruleTableRecordDO.getUserId() == null) {
                        return ruleTableRecordDO; // 如果 userId 为 null，也返回 ruleTableRecordDO
                    } else {
                        throw new ServiceException(NO_PERMISSION_ERROR); // 否则抛出异常
                    }
                });
    }

    // 校验与获取一并进行，用户只能自己创建的的大规则记录
    private RuleTableRecordDO getUserRuleTableRecord(Long ruleTableRecordId) {
        RuleTableRecordDO ruleTableRecordDO = distributedCache.get(
                RULE_TABLE_KEY + ruleTableRecordId,
                RuleTableRecordDO.class,
                () -> ruleTableMapper.selectOne(new LambdaQueryWrapper<RuleTableRecordDO>()
                        .eq(RuleTableRecordDO::getUserId, UserContext.getUserId())
                        .eq(RuleTableRecordDO::getId, ruleTableRecordId)),
                TIME_OUT_OF_SECONDS,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(ruleTableRecordDO).orElseThrow(() -> new ServiceException(RULE_TABLE_NOT_EXIST_ERROR));

        return ruleTableRecordDO;
    }


    //分页获取大规则，加模糊查找功能(只能获取系统的以及当前登录用户的)
    public PageResponse<RuleTableRecordRespDTO> getRuleTablesByPage(PageRequestExtend pageRequestExtend) {
        IPage<RuleTableRecordDO> page = new Page<>(pageRequestExtend.getCurrent(), pageRequestExtend.getSize());

        LambdaQueryWrapper<RuleTableRecordDO> queryWrapper =
                Optional.ofNullable(pageRequestExtend.getWords())
                        .filter(words -> !words.isEmpty())
                        .map(words -> new LambdaQueryWrapper<RuleTableRecordDO>()
                                .like(RuleTableRecordDO::getName, words)
                                .and(wrapper ->
                                        wrapper.isNull(RuleTableRecordDO::getUserId)
                                                .or()
                                                .eq(RuleTableRecordDO::getUserId, UserContext.getUserId())
                                ))
                        .orElseGet(() -> new LambdaQueryWrapper<RuleTableRecordDO>()
                                .isNull(RuleTableRecordDO::getUserId)
                                .or()
                                .eq(RuleTableRecordDO::getUserId, UserContext.getUserId()));

        IPage<RuleTableRecordDO> resultPage = ruleTableMapper.selectPage(page, queryWrapper);
        List<RuleTableRecordRespDTO> convertedRecords = resultPage.getRecords()
                .stream()
                .map(
                        ruleTableRecordDO -> RuleTableRecordRespDTO.builder()
                                .id(ruleTableRecordDO.getId())
                                .name(ruleTableRecordDO.getName())
                                .description(ruleTableRecordDO.getDescription())
                                .createdSource(ruleTableRecordDO.getCreatedSource())
                                .build()
                )
                .collect(Collectors.toList());

        return new PageResponse<>(resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getTotal(),
                convertedRecords);
    }


    //删除一个大规则
    public void deleteRuleTableRecord(Long ruleTableRecordId) {
        RuleTableRecordDO legalRuleTableRecordDO = getUserRuleTableRecord(ruleTableRecordId);

        List<RelatedRuleRecordDO> relatedRuleRecordDOS = relatedRuleMapper.selectList(new LambdaQueryWrapper<RelatedRuleRecordDO>()
                .eq(RelatedRuleRecordDO::getRuleTableId, ruleTableRecordId));

        relatedRuleRecordDOS
                .stream()
                .filter(relatedRuleRecordDO -> distributedCache.get(
                        RULES_DETAIL_KEY + relatedRuleRecordDO.getRuleDetailId(),
                        RulesDetailRecordDO.class,
                        () -> rulesDetailMapper.selectOne(new LambdaQueryWrapper<RulesDetailRecordDO>()
                                .eq(RulesDetailRecordDO::getId, relatedRuleRecordDO.getRuleDetailId())),
                        TIME_OUT_OF_SECONDS,
                        TimeUnit.SECONDS
                ).getCreatedSource().equals(USER_CREATE_CODE))
                .forEach(
                        relatedRuleRecordDO -> {
                            int delete1 = rulesDetailMapper.deleteById(relatedRuleRecordDO.getRuleDetailId());
                            int delete2 = relatedRuleMapper.deleteById(relatedRuleRecordDO.getId());
                            Boolean delete3 = distributedCache.delete(RULES_DETAIL_KEY + relatedRuleRecordDO.getRuleDetailId());
                            //
                        }
                );

        int delete4 = ruleTableMapper.deleteById(ruleTableRecordId);
        if (!SqlHelper.retBool(delete4)) {
            throw new ServiceException(RULE_TABLE_NOT_EXIST_ERROR);
        }
    }


    //增加一个大规则
    public void addRuleTableRecord(RuleTableRecordAddDTO ruleTableRecordAddDTO) {
        RuleTableRecordDO ruleTableRecordDO = RuleTableRecordDO.builder()
                .name(ruleTableRecordAddDTO.getName())
                .description(ruleTableRecordAddDTO.getDescription())
                .userId(UserContext.getUserId())
                .createdSource(USER_CREATE_CODE)
                .build();

        int insert = ruleTableMapper.insert(ruleTableRecordDO);

        if (!SqlHelper.retBool(insert)) {
            throw new ServiceException(RULE_TABLE_INSERT_ERROR);
        }
    }

    //更新一个大规则
    public void updateRuleTableRecord(RuleTableRecordUpdateDTO ruleTableRecordUpdateDTO) {
        RuleTableRecordDO legalRuleTableRecordDO = getUserRuleTableRecord(ruleTableRecordUpdateDTO.getId());

        RuleTableRecordDO newRuleTableRecordDO = RuleTableRecordDO.builder()
                .id(ruleTableRecordUpdateDTO.getId())
                .name(ruleTableRecordUpdateDTO.getName())
                .description(ruleTableRecordUpdateDTO.getDescription())
                .userId(UserContext.getUserId())
                .createdSource(USER_CREATE_CODE)
                .build();

        int update = ruleTableMapper.updateById(newRuleTableRecordDO);
        if (!SqlHelper.retBool(update)) {
            throw new ServiceException(RULE_TABLE_UPDATE_ERROR);
        }

        distributedCache.delete(RULE_TABLE_KEY + ruleTableRecordUpdateDTO.getId()); // 需要判断重复删除？
    }


    //获取一个大规则详情（包含大规则下的所有小规则）
    public RuleTableWithDetailRespDTO getRulesDetailsByRuleTableRecord(Long ruleTableRecordId) {
        RuleTableRecordDO ruleTableRecordDO = getLegalRuleTableRecord(ruleTableRecordId);

        RuleTableWithDetailRespDTO ruleTableWithDetailRespDTO = new RuleTableWithDetailRespDTO();
        BeanUtils.copyProperties(ruleTableRecordDO, ruleTableWithDetailRespDTO);

        List<RulesDetailRecordRespDTO> rulesDetailsByRuleTableRecordId = getRulesDetailsByRuleTableRecordId(ruleTableRecordId);
        ruleTableWithDetailRespDTO.setRulesDetailRecords(rulesDetailsByRuleTableRecordId);

        return ruleTableWithDetailRespDTO;
    }


    //通过大规则id获取所属的所有小规则
    public List<RulesDetailRecordRespDTO> getRulesDetailsByRuleTableRecordId(Long ruleTableRecordId) {
        getLegalRuleTableRecord(ruleTableRecordId);// 检查是否正确

        List<RulesDetailRecordRespDTO> collect = relatedRuleMapper.selectList(new LambdaQueryWrapper<RelatedRuleRecordDO>()
                        .eq(RelatedRuleRecordDO::getRuleTableId, ruleTableRecordId))
                .stream()
                .map(relatedRuleRecordDO -> distributedCache.get(
                        RULES_DETAIL_KEY + relatedRuleRecordDO.getRuleDetailId(),
                        RulesDetailRecordDO.class,
                        () -> rulesDetailMapper.selectOne(
                                new LambdaQueryWrapper<RulesDetailRecordDO>()
                                        .eq(RulesDetailRecordDO::getId, relatedRuleRecordDO.getRuleDetailId())
                        ),
                        TIME_OUT_OF_SECONDS,
                        TimeUnit.SECONDS
                ))
                .map(rulesDetailRecordDO -> {
                    RulesDetailRecordRespDTO rulesDetailRecordRespDTO = new RulesDetailRecordRespDTO();
                    BeanUtils.copyProperties(rulesDetailRecordDO, rulesDetailRecordRespDTO);
                    return rulesDetailRecordRespDTO;
                })
                .collect(Collectors.toList());

        return collect;
    }

}
