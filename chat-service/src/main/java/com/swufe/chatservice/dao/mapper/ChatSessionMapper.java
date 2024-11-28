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
import com.swufe.chatservice.dao.entity.ChatSessionDO;
import com.swufe.chatservice.dto.resp.ChatSessionRespDTO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话信息持久层
 */
public interface ChatSessionMapper extends BaseMapper<ChatSessionDO> {
    @Select("SELECT session_id,user_id,lawyer_id, session_type, status, create_time " +
            "FROM chat_sessions WHERE user_id = #{userId}")
    List<ChatSessionRespDTO> selectSessionsByUserId(@Param("userId") String userId);


}
