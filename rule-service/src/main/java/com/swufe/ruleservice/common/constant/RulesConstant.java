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

package com.swufe.ruleservice.common.constant;

/**
 * Redis Key 定义常量类
 */
public final class RulesConstant {
    // 项目访问前缀
    public static final String PROJECT_CONTEXT_PATH = "/api/rule-service";

    // 状态相关
    public static final int SYSTEM_CREATE_CODE = 0;
    public static final int USER_CREATE_CODE = 1;

    // redis过期时间
    public static final int TIME_OUT_OF_SECONDS = 7200;

    // redis存放的key前缀
    public static final String RULE_TABLE_KEY = "contract-review-rule-service-rule-table-record";
    public static final String RULES_DETAIL_KEY = "contract-review-rule-service-small-rule-info";

}
