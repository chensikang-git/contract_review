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

package com.swufe.userservice.controller;

import com.swufe.chatlaw.Results;
import com.swufe.chatlaw.result.Result;

import com.swufe.userservice.dto.UserLoginDTO;
import com.swufe.userservice.dto.UserLogoutDTO;
import com.swufe.userservice.dto.UserRegisterDTO;
import com.swufe.userservice.dto.UserUpdateDTO;
import com.swufe.userservice.service.UserLoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户登录控制层
 */
@RestController
@RequiredArgsConstructor
public class UserLoginController {

    private final UserLoginService userLoginService;


    /**
     *  用户注册
     */
    @PostMapping("/api/user-service/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userLoginService.register(userRegisterDTO);
        return Results.success();
    }

    /**
     *  用户登录
     */
    @PostMapping("/api/user-service/login")
    public Result<String> login (@Valid @RequestBody UserLoginDTO userLoginDTO){
        return Results.success(userLoginService.login(userLoginDTO));
    }

    /**
     * 修改用户信息
     */
    @PostMapping("/api/user-service/update")
    public Result<Void> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
            userLoginService.updateUser(userUpdateDTO);
            return Results.success();
    }

    /**
     * 用户上传头像
     */
    @PostMapping("/api/user-service/uploadPicture")
    public Result<String> uploadPicture(MultipartFile file) {
        return Results.success(userLoginService.uploadPicture(file));
    }

    /**
     * 注销用户登录
     */
    @PostMapping("/api/user-service/logout")
    public Result<Void> logout(@Valid @RequestBody UserLogoutDTO userLogoutDTO) {
//        userLoginService.logout(userLogoutDTO);
        return Results.success();
    }
}
