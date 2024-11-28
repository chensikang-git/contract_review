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

package com.swufe.userservice.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户查询返回无脱敏参数
 */
@Data
public class AccessTokenRespDTO {

    /**
     * 调用凭证
     */
    @JsonProperty("access_token") // 指定 JSON 中的字段名与 Java 字段的映射
    private String accessToken;

    /**
     * 过期时间
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;


}
