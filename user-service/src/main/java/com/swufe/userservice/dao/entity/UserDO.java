package com.swufe.userservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swufe.chatlaw.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
@Data
@TableName("user") // 对应数据库的 user 表
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDO extends BaseDO {

    @TableId(type = IdType.AUTO) // 指定自增主键策略
    private Long userId;

    /**
     * 用户电话
     */
    private String phoneNumber;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;


    /**
     * 用户头像 URL
     */
    private String userPic;

}