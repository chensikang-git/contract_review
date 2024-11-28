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

package com.swufe.chatservice.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swufe.chatservice.dao.entity.UserDO;
import com.swufe.chatservice.dto.resp.UserLawyerRespDTO;
import com.swufe.chatservice.toolkit.FastJsonListTypeHandler;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * 用户信息持久层
 */
public interface UserMapper extends BaseMapper<UserDO> {
    @Select("SELECT u.openid, u.role, u.username, u.contact, u.photo_url, " +
            "l.law_firm, l.domain, l.sex, l.real_name, l.graduate_school, " +
            "l.first_occupation, l.work_firm, l.present_position, l.professional_photo," +
            "l.address,l.card "+
            "FROM user u LEFT JOIN lawyer l ON u.openid = l.openid " +
            "WHERE u.openid = #{openid}")
    @Results({
            @Result(column = "domain", property = "domain", typeHandler = FastJsonListTypeHandler.class)
    })
    UserLawyerRespDTO getUserAndLawyerInfo(String openid);

}
