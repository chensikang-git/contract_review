package com.swufe.ruleservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.swufe.chatlaw.DistributedCache;
import com.swufe.chatlaw.exception.ClientException;
import com.swufe.chatlaw.exception.ServiceException;
import com.swufe.ruleservice.dao.entity.RelatedRuleDO;
import com.swufe.ruleservice.dao.entity.SmallRuleDO;
import com.swufe.ruleservice.dao.mapper.RelatedRuleMapper;
import com.swufe.ruleservice.dao.mapper.RuleMapper;
import com.swufe.ruleservice.dto.req.SmallRuleReqDTO;
import com.swufe.ruleservice.dto.req.UpdateSmallRuleReqDTO;
import com.swufe.ruleservice.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.swufe.chatlaw.errorcode.BaseErrorCode.*;
import static com.swufe.ruleservice.common.constant.RedisKeyConstant.RULE_KEY;
import static org.springframework.data.util.Optionals.ifPresentOrElse;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final RuleMapper ruleMapper;
    private final DistributedCache distributedCache;
    private final RelatedRuleMapper relatedRuleMapper;

    @Override
    public void addSmallRule(SmallRuleReqDTO smallRuleReqDTO) {
        //先判断是否有小规则重名
        SmallRuleDO smallRuleDO = distributedCache.get(
                RULE_KEY + smallRuleReqDTO.getName(),
                SmallRuleDO.class,
                () -> ruleMapper.selectOne(
                        new LambdaQueryWrapper<SmallRuleDO>().eq(SmallRuleDO::getName, smallRuleReqDTO.getName())
                ),
                7200,
                TimeUnit.SECONDS
        );
        //重名报错
        Optional.ofNullable(smallRuleDO).ifPresent(e -> {
            throw new ClientException(RULE_NAME_EXIST_ERROR);
        });
        //不重名插入小规则表
        smallRuleDO = BeanUtil.toBean(smallRuleReqDTO, SmallRuleDO.class);
        System.out.println("!!!!!!!!!!!!!!!smallRuleDO: " + smallRuleDO);
        int insert = ruleMapper.insert(smallRuleDO);
        if (!SqlHelper.retBool(insert)) {
            throw new ServiceException(INSERT_ERROR);
        }

        // 插入成功并且返回id
        Long ruleDetailId = smallRuleDO.getId(); // 获取自动生成的小规则ID
        System.out.println("ruleTableId ID: " + ruleDetailId);

        //获取大规则id
        Long ruleTableId = smallRuleReqDTO.getRuleTableId();

        //插入关联表
        RelatedRuleDO relatedRule = RelatedRuleDO.builder()
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
        SmallRuleDO smallRuleDO = distributedCache.get(
                RULE_KEY + smallRuleId,
                SmallRuleDO.class,
                () -> ruleMapper.selectOne(
                        new LambdaQueryWrapper<SmallRuleDO>().eq(SmallRuleDO::getId, smallRuleId)
                ),
                7200,
                TimeUnit.SECONDS
        );
        Optional.ofNullable(smallRuleDO).
                ifPresentOrElse(
                        u -> {
                            //检查数据是否安全
                            // 检查来源字段是否为1
                            if (u.getCreatedSource() != null && u.getCreatedSource() == 1) {
                                throw new ClientException("数据异常");
                            }
                            //去关联表中找到对应的大规则id
                            Long bigRuleId = relatedRuleMapper.selectOne(new LambdaQueryWrapper<RelatedRuleDO>()
                                    .eq(RelatedRuleDO::getRuleDetailId, u.getId())).getRuleTableId();
                            if (bigRuleId == null) {
                                throw new ClientException("数据异常");
                            }
                            //检查大规则表中的userid是否存在
                            if (ruleMapper.selectById(bigRuleId).getId() == null) {
                                throw new ClientException("数据异常");
                            }

                            // 数据安全 可以删除 related_rule 表中 ruleDetailId 与 smallRuleDO.getId() 相同的记录
                            relatedRuleMapper.delete(new LambdaQueryWrapper<RelatedRuleDO>()
                                    .eq(RelatedRuleDO::getRuleDetailId, u.getId()));

                            // 删除缓存中的数据
                            distributedCache.delete(RULE_KEY + smallRuleId);

                            // 删除数据库中的数据
                            ruleMapper.deleteById(smallRuleDO.getId());

                        },
                        () -> {
                            // 如果小规则不存在，抛出异常
                            throw new ClientException(SMALL_RULE_DISAPPEAR_ERROR);
                        }
                );
    }

    @Override
    public void updateSmallRule(UpdateSmallRuleReqDTO updateSmallRuleReqDTO) {
        SmallRuleDO existingSmallRuleDO = ruleMapper.selectById(updateSmallRuleReqDTO.getId());

        if (existingSmallRuleDO == null) {
            throw new ClientException(SMALL_RULE_DISAPPEAR_ERROR);
        }

        // 更新小规则信息
        BeanUtil.copyProperties(updateSmallRuleReqDTO, existingSmallRuleDO);

        // 更新数据库中的小规则
        int updateResult = ruleMapper.updateById(existingSmallRuleDO);
        if (!SqlHelper.retBool(updateResult)) {
            throw new ServiceException(INSERT_ERROR);
        }

        // 删除缓存中的小规则信息
        distributedCache.delete(RULE_KEY + updateSmallRuleReqDTO.getId());
    }


}
