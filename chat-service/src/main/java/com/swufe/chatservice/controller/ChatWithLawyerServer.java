package com.swufe.chatservice.controller;

import com.alibaba.fastjson.JSON;
import com.swufe.chatservice.dto.req.SessionMessageDto;
import com.swufe.chatservice.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value="/api/chat-service/chat-with-lawyer/{token}")
@Slf4j
@Data
public class ChatWithLawyerServer {

    private static ChatService chatService;

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, ChatWithLawyerServer> chatWebSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收的username
     */
    private String userId = "";

    private String lawyerId = "";
    /**
     * 接收的sessionId
     */
    private Long sessionId ;

    @Resource
    public void setChatModel(ChatService chatService) {
        ChatWithLawyerServer.chatService = chatService;
    }


    /**
     * 建立连接
     * @param session 会话
     * @param token 连接token
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("token") String token) throws IOException {
        this.session = session;
        this.userId = chatService.authenticateUser(session, token);
        chatWebSocketMap.put(userId, this);
        Map<String, List<String>> params = session.getRequestParameterMap();
        // 从请求参数中安全地获取 sessionId，如果为空则设置为 0
        String sessionIdStr = params.getOrDefault("sessionId", List.of("")).get(0);
        Long sessionId = (sessionIdStr != null && !sessionIdStr.trim().isEmpty()) ? Long.valueOf(sessionIdStr) : 0L;
        // 安全地获取 lawyerId，并判断是否为空
        this.lawyerId = params.getOrDefault("lawyerId", List.of("")).get(0);
        if (lawyerId == null || lawyerId.trim().isEmpty()) {
            // 如果 lawyerId 为空或无效，关闭会话
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "律师的id必须正确"));
            return; // 终止连接
        }
        this.sessionId =chatService.findSession(sessionId,session,userId,lawyerId);
        sendMessage(JSON.toJSONString(new SessionMessageDto(this.sessionId, "session start")));
        chatService.sendTime(chatWebSocketMap,userId,this.sessionId);
    }

    @OnClose
    public void onClose() {
        chatWebSocketMap.remove(userId);
        log.info("{}--close",userId);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println(userId + "--" + message);
        chatService.sendToUser(message,chatWebSocketMap,userId,lawyerId);

        // 使用 lambda 表达式将 sendMessage 传递给 testModel
//        chatService.testModel(message,this.sessionId, msg -> {
//            try {
//                sendMessage(msg);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendInfo(String message, String toUserId) throws IOException {
        chatWebSocketMap.get(toUserId).sendMessage(message);
    }

}