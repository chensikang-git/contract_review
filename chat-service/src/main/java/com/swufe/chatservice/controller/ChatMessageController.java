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

package com.swufe.chatservice.controller;

import com.swufe.chatlaw.Results;
import com.swufe.chatlaw.page.PageRequest;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.chatlaw.result.Result;
import com.swufe.chatservice.dto.req.ChatMessageDTO;
import com.swufe.chatservice.dto.resp.ChatMessageRespDTO;
import com.swufe.chatservice.dto.resp.ChatSessionRespDTO;
import com.swufe.chatservice.dto.resp.ReportRespDTO;
import com.swufe.chatservice.dto.resp.UserLawyerRespDTO;
import com.swufe.chatservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户登录控制层
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatService chatService;

    @PostMapping("/api/chat-service/upload-message")
    public Result<Void> uploadMessage(@Valid @RequestBody ChatMessageDTO chatMessageDTO) {
        chatService.uploadMessage(chatMessageDTO);
        return Results.success();
    }

    @GetMapping("/api/chat-service/generate-report")
    public Result<ReportRespDTO> generateReport(@RequestParam(required = true)  Long sessionId) {
        return Results.success(chatService.generateReport(sessionId));
    }

    @GetMapping("/api/chat-service/matching-lawyer")
    public Result<List<UserLawyerRespDTO>> matchingLawyer(@RequestParam(required = true)  Long sessionId) {
        return Results.success(chatService.matchingLawyer(sessionId));
    }

    @GetMapping("/api/chat-service/get-chat-list")
    public Result<PageResponse<ChatSessionRespDTO>> getChatList( PageRequest pageRequest) {
        return Results.success(chatService.getChatList(pageRequest));
    }

    @GetMapping("/api/chat-service/get-chat-detail")
    public Result<List<ChatMessageRespDTO>> getChatDetail(@RequestParam(required = true)  Long sessionId) {
        return Results.success(chatService.getChatDetail(sessionId));
    }

}
