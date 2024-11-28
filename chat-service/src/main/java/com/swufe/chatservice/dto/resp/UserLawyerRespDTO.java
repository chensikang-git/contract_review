package com.swufe.chatservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLawyerRespDTO {
    private String role; // 用户角色
    private String openId;

    // 律师相关信息
    private String lawFirm; // 律师事务所
    private List<String> domain; // 律师专长
    /**
     * 职业资格证编号
     */
    private String card;
    /**
     * 律师地址
     */
    private String address;
    private String sex; // 性别
    private String realName; // 真实姓名
    private String graduateSchool; // 毕业院校
    private String firstOccupation; // 初次职业
    private String workFirm; // 就职律所
    private String presentPosition; // 目前职位
    private String professionalPhoto; // 专业照片
}