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

package com.swufe.userservice.remote;

import com.swufe.chatlaw.result.Result;
import com.swufe.userservice.remote.dto.AccessTokenRespDTO;
import com.swufe.userservice.remote.dto.LoginWechatRespDTO;
import com.swufe.userservice.remote.dto.PhoneInfoRespDTO;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 用户远程服务调用
 */
@FeignClient(name = "wechat", url = "https://api.weixin.qq.com")
public interface UserRemoteService {

    /**
     * 根据小程序生成access_token
     */
    @GetMapping("/cgi-bin/token?grant_type=client_credential")
    AccessTokenRespDTO getAccessToken(
            @RequestParam("appid") String appId,
            @RequestParam("secret") String secret
    );
    /**
     * 远程调用获取手机号
     */
    @PostMapping("/wxa/business/getuserphonenumber?access_token={accessToken}")
    PhoneInfoRespDTO getPhoneNumber(
            @PathVariable("accessToken") String accessToken,
            @RequestBody Map<String, String> codeData
    );
    /**
     * 远程调用获取登录openid
     */
    @GetMapping("/sns/jscode2session?grant_type=authorization_code")
    String remoteLogin(
            @RequestParam("appid") String appId,
            @RequestParam("secret") String secret,
            @RequestParam("js_code") String jsCode
    );


}
