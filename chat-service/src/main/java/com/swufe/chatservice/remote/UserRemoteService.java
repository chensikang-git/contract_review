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

package com.swufe.chatservice.remote;

import com.swufe.chatservice.dto.req.SubscribeMessageReqDTO;
import com.swufe.chatservice.remote.dto.AccessTokenRespDTO;
import com.swufe.chatservice.remote.dto.SubscribeMessageRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


/**
 * 用户远程服务调用
 */
@FeignClient(name = "chat-law--user-service")
public interface UserRemoteService {

    @GetMapping("/api/user-service/get-user-info")
    AccessTokenRespDTO getAccessToken(
            @RequestParam("appid") String appId,
            @RequestParam("secret") String secret
    );



}
