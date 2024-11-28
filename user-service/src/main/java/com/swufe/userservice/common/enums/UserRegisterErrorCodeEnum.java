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

package com.swufe.userservice.common.enums;

import com.swufe.chatlaw.errorcode.IErrorCode;
import lombok.AllArgsConstructor;

/**
 * 用户注册错误码枚举
 */
@AllArgsConstructor
public enum UserRegisterErrorCodeEnum implements IErrorCode {

    USER_REGISTER_FAIL("A006000", "用户登录失败"),

    USER_NAME_NOTNULL("A006001", "用户名不能为空"),

    PHONE_NOTNULL("A006003", "手机号不能为空");


    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误提示消息
     */
    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
