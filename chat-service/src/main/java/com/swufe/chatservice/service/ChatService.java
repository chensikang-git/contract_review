package com.swufe.chatservice.service;

import com.swufe.chatlaw.page.PageRequest;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.chatservice.controller.ChatWithLawyerServer;
import com.swufe.chatservice.dto.req.ChatMessageDTO;
import com.swufe.chatservice.dto.resp.ChatMessageRespDTO;
import com.swufe.chatservice.dto.resp.ChatSessionRespDTO;
import com.swufe.chatservice.dto.resp.ReportRespDTO;
import com.swufe.chatservice.dto.resp.UserLawyerRespDTO;
import jakarta.websocket.Session;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public interface ChatService {
    /**
     * 验证token返回UserId
     */
    public String authenticateUser(Session session, String token);

    /**
     * 判断是否需要创建新的会话
     */
    public Long findSession(long sessionId, Session session, String userId, String lawyerId);
    public Long findSession(long sessionId, Session session, String userId);


    void testModel(String message, Long sessionId, Consumer<String> sender);

    void uploadMessage(ChatMessageDTO chatMessageDTO);

    ReportRespDTO generateReport(Long sessionId);

    List<UserLawyerRespDTO> matchingLawyer(Long sessionId);

    PageResponse<ChatSessionRespDTO> getChatList(PageRequest pageRequest);

    List<ChatMessageRespDTO> getChatDetail(Long sessionId);

    void sendToUser(String message, ConcurrentHashMap<String, ChatWithLawyerServer> chatWebSocketMap,String userId,String lawyerId) throws IOException;

    void sendTime(ConcurrentHashMap<String, ChatWithLawyerServer> chatWebSocketMap, String sessionId, Long id);
}
