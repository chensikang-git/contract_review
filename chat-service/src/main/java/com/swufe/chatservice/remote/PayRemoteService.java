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
import com.swufe.chatservice.dto.req.WeChatPayReqDTO;
import com.swufe.chatservice.remote.dto.AccessTokenRespDTO;
import com.swufe.chatservice.remote.dto.SubscribeMessageRespDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 用户远程服务调用
 */
@FeignClient(name = "wechatPay", url = "https://api.mch.weixin.qq.com")
public interface PayRemoteService {

    /**
     * 远程调用获取手机号
     */
    @PostMapping(value="/v3/pay/transactions/jsapi")
    @Headers("Accept: application/json")
    Map<String,String> PayForPreOrder(
//            @RequestHeader("Authorization") String authToken,
            @RequestBody WeChatPayReqDTO weChatPayReqDTO
    );

//    @GetMapping("/cgi-bin/token?grant_type=client_credential")
//    Map<String,String> getAccessToken(
//            @RequestParam("appid") String appId,
//            @RequestParam("secret") String secret
//    );



}
