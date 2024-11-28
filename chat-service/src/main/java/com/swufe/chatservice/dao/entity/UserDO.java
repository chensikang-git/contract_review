package com.swufe.chatservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("user") // 对应数据库的 user 表
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDO extends BaseDO {

    /**
     * 微信用户唯一标识
     */
    @TableId(value = "openid", type = IdType.INPUT) // 使用输入的 openid 作为主键
    private String openid;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 用户名
     */
    private String username;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 用户头像 URL
     */
    private String photoUrl;

}