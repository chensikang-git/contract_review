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

package com.swufe.userservice.service;


import com.swufe.userservice.dto.UserLoginDTO;
import com.swufe.userservice.dto.UserLogoutDTO;
import com.swufe.userservice.dto.UserRegisterDTO;
import com.swufe.userservice.dto.UserUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;


public interface UserLoginService {
    //用户注册
    void register(@Valid  UserRegisterDTO userRegisterDTO);

    //用户登录
    String login(@Valid UserLoginDTO userLoginDTO);

    //更新用户信息
    void updateUser(@Valid UserUpdateDTO userUpdateDTO);

    //用户上传头像
    String uploadPicture(MultipartFile file);
//
//    //注销用户登录
//    void logout(@Valid UserLogoutDTO userLogoutDTO);
}
