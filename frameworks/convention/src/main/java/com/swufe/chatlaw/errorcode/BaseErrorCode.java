/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swufe.chatlaw.errorcode;

/**
 * 基础错误码定义
 */
public enum BaseErrorCode implements IErrorCode {

    // ========== 一级宏观错误码 客户端错误 ==========
    CLIENT_ERROR("A000001", "用户端错误"),
    INSERT_ERROR("A000002", "修改失败，请稍后再试"),
    // ========== 二级宏观错误码 用户注册或登录错误 ==========
    USER_REGISTER_ERROR("A000100", "用户注册错误"),
    USER_REGISTER_NAMED_ERROR("A000101", "用户名被占用"),
    USER_NAME_VERIFY_ERROR("A000110", "用户校验失败"),
    USER_NAME_EXIST_ERROR("A000111", "用户已存在"),
    USER_NAME_NOT_EXIST_ERROR("A000112", "用户不存在"),
    USER_PASSWORD_ERROR("A000113", "密码错误"),
    USER_NAME_SENSITIVE_ERROR("A000114", "用户名包含敏感词"),
    USER_NAME_SPECIAL_CHARACTER_ERROR("A000113", "用户名包含特殊字符"),
    PICTURE_EMPTY_ERROR("A000116","上传图片文件失败，文件为空"),

    RULE_NAME_EXIST_ERROR("A000211", "规则名已存在"),
    DELETE_SOURCE_ERROR("A000212", "系统创建不可删除"),
    SMALL_RULE_DISAPPEAR_ERROR("A000211", "规则名已存在"),

    FILE_TYPE_ERROR("C000113", "文件格式不正确，请检查文件格式"),
    FILE_TRANS_ERROR("A000114", "服务器文件复制错误"),
    FILE_ADD_ERROR("A000115", "服务器新增文件错误"),
    FILE_NOT_FOUND_ERROR("A000116", "服务器文件不存在错误"),
    TENCENT_COS_ERROR("A000117", "上传失败，腾讯cos异常"),


    // ========== 二级宏观错误码 系统请求缺少幂等Token ==========
    IDEMPOTENT_TOKEN_NULL_ERROR("A000200", "幂等Token为空"),
    IDEMPOTENT_TOKEN_DELETE_ERROR("A000201", "幂等Token已被使用或失效"),

    // ========== 一级宏观错误码 系统执行出错 ==========
    SERVICE_ERROR("B000001", "系统执行出错"),
    REDIS_CLEAN_ERROR("B000002", "缓存清理失败"),
    // ========== 二级宏观错误码 系统执行超时 ==========
    SERVICE_TIMEOUT_ERROR("B000100", "系统执行超时"),

    // ========== 一级宏观错误码 调用第三方服务出错 ==========
    REMOTE_ERROR("C000001", "调用第三方服务出错");

    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
