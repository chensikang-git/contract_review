package com.swufe.chatservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.swufe.chatlaw.DistributedCache;
import com.swufe.chatlaw.core.CacheLoader;
import com.swufe.chatlaw.core.UserContext;
import com.swufe.chatlaw.exception.ClientException;
import com.swufe.chatlaw.exception.ServiceException;
import com.swufe.chatlaw.page.PageRequest;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.chatlaw.toolkit.BeanUtil;
import com.swufe.chatlaw.toolkit.JWTUtil;
import com.swufe.chatservice.controller.ChatWithLawyerServer;
import com.swufe.chatservice.dao.entity.ChatMessageDO;
import com.swufe.chatservice.dao.entity.ChatSessionDO;
import com.swufe.chatservice.dao.entity.LawyerDO;
import com.swufe.chatservice.dao.mapper.ChatMessageMapper;
import com.swufe.chatservice.dao.mapper.ChatSessionMapper;
import com.swufe.chatservice.dao.mapper.LawyerMapper;
import com.swufe.chatservice.dao.mapper.UserMapper;
import com.swufe.chatservice.dto.req.AiAnswerReqDTO;
import com.swufe.chatservice.dto.req.ChatMessageDTO;
import com.swufe.chatservice.dto.req.SubscribeMessageReqDTO;
import com.swufe.chatservice.dto.resp.*;
import com.swufe.chatservice.remote.ChatRemoteService;
import com.swufe.chatservice.remote.ModelRemoteService;
import com.swufe.chatservice.remote.dto.CaseTypeRespDTO;
import com.swufe.chatservice.remote.req.ChatRecordReqDTO;
import com.swufe.chatservice.service.ChatService;
import com.swufe.chatservice.service.sse.SseService;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.swufe.chatlaw.core.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import reactor.core.publisher.Flux;

import static com.swufe.chatlaw.constant.MiniProgramConstant.*;
import static com.swufe.chatservice.common.constant.ChatConstant.*;
import static com.swufe.chatservice.common.constant.RedisKeyConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final LawyerMapper lawyerMapper;
    private final DistributedCache distributedCache;
    private final ChatRemoteService chatRemoteService;
    private final ModelRemoteService modelRemoteService;
    private final UserMapper userMapper;
    private final SseService sseService;



    @Override
    public String authenticateUser(Session session, String token) {
        return Optional.ofNullable(JWTUtil.parseJwtToken(token))
                .map(UserInfoDTO::getUserId)
                .orElseThrow(() -> {
                    try {
                        session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid or missing token"));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to close session", e);
                    }
                    return new RuntimeException("Invalid or missing token");
                });
    }


    public Long findSession(long sessionId, Session session, String userId) {
        return findSession( sessionId,  session,  userId,null);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long findSession(long sessionId, Session session, String userId, String lawyerId) {
        if (sessionId != 0) {
            // sessionId 不为 0，尝试查找现有会话
            ChatSessionDO existingSession =distributedCache.get(SESSION_KEY+sessionId,ChatSessionDO.class,new CacheLoader<ChatSessionDO>() {
                @Override
                public ChatSessionDO load() {
                    return chatSessionMapper.selectById(sessionId);
                }
            },7200, TimeUnit.SECONDS);

            if (existingSession != null) {
                // 如果会话存在，检查状态
                if (StrUtil.equals(existingSession.getStatus(), STATUS_ENDED)) {
                    try {
                        session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Session ended"));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to close WebSocket session", e);
                    }
                    return null; // 连接关闭，返回 null
                }
                return existingSession.getSessionId(); // 返回现有的 sessionId
            }
        }
        String realName = Optional.ofNullable(lawyerId)
                .map(id -> {
                    LawyerDO lawyerDO = lawyerMapper.selectOne(new QueryWrapper<LawyerDO>().eq("openid", lawyerId));
                    return lawyerDO.getRealName();
                })
                .orElse(CASE_TYPE_DEFAULT);


        // 使用 Optional 直接连续调用构建新的 ChatSessionDO 对象
        ChatSessionDO chatSessionDO = ChatSessionDO.builder()
                .userId(userId)
                .sessionType(Optional.ofNullable(lawyerId).isPresent() ? SESSION_TYPE_LAWYER : SESSION_TYPE_MODEL) // 根据 lawyerId 是否存在，设置不同的会话类型
                .status(STATUS_ACTIVE) // 设置会话状态
                .lawyerId(lawyerId)
                .caseType(realName)
                .build();

        chatSessionMapper.insert(chatSessionDO); // 插入新会话并自动生成 ID
        return chatSessionDO.getSessionId(); // 返回新创建的 session
    }

    //传入十个问题 和 进行案例类型分析
    @Override
    public void testModel(String message, Long sessionId,Consumer<String> sender) {
        ChatSessionDO existingSession =distributedCache.get(SESSION_KEY+sessionId,ChatSessionDO.class,new CacheLoader<ChatSessionDO>() {
            @Override
            public ChatSessionDO load() {
                return chatSessionMapper.selectById(sessionId);
            }
        },7200, TimeUnit.SECONDS);
        ChatRecordReqDTO chatRecordReqDTO = generateRecord(sessionId);
        chatRecordReqDTO.getDialogues().add(new ChatRecordReqDTO.Dialogue("user",message));//将新的消息存到dialogue中
        Optional.ofNullable(existingSession.getTenQuestions())
                .filter(tenQuestions -> tenQuestions != null && !tenQuestions.trim().isEmpty())  // 如果不为空字符串或null，直接返回
                .orElseGet(() -> {
                    // 如果为空或null，生成新的问题
                    TenQuestionsRespDTO tenQuestionsRespDTO = modelRemoteService.generateTenQuestions(chatRecordReqDTO);
                    System.out.println(tenQuestionsRespDTO);
                    if(tenQuestionsRespDTO.isFlag()){
                        existingSession.setTenQuestions(tenQuestionsRespDTO.getQuestions());
                        chatSessionMapper.updateById(existingSession);
                        distributedCache.delete(SESSION_KEY + sessionId);
                    }
                    return "";  // 返回新生成的问题
                });
        System.out.println(chatRecordReqDTO.getDialogues().size());
        if (chatRecordReqDTO.getDialogues().size() == 1 || chatRecordReqDTO.getDialogues().size() % 10 == 0) {
            // 启动一个新线程并传入 existingSession
            Thread thread = new Thread(() -> {
                CaseTypeRespDTO caseTypeRespDTO = modelRemoteService.analyzeCaseType(chatRecordReqDTO);
                if(caseTypeRespDTO.getEnoughInformation()){
                    existingSession.setCaseType(caseTypeRespDTO.getCaseType());
                    chatSessionMapper.updateById(existingSession);
                    distributedCache.delete(SESSION_KEY + sessionId);
                }
            });
            thread.start();  // 启动线程
        }

        Flux<String> problemStream = sseService.streamProblem(new AiAnswerReqDTO(chatRecordReqDTO.getDialogues(),existingSession.getTenQuestions()));
        problemStream.doOnNext(problem -> {
            try {
//                Optional.ofNullable(problem.getEnd()).ifPresentOrElse(
//                        (end)->{
//                            sender.accept("[DONE]");  // 每次发送一个字符
//                        },
//                        ()->{
//                            System.out.println(problem);
//                            if(problem.getPart()!=null){
//                                sender.accept(problem.getMessage());
//                            }
//                        }
//                );
                sender.accept(problem);

            } catch (Exception e) {
                sender.accept("[DONE]");
                e.printStackTrace();
            }
        }).subscribe();
////        TODO 调用外部接口把List传入进去然后websocket返回
//        for (char ch : message.toCharArray()) { // 将消息转换为字符数组逐个字符发送
//            try {
//                sender.accept(String.valueOf(ch));  // 每次发送一个字符
//                Thread.sleep(100);  // 模拟延迟，100毫秒发送一次
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();  // 恢复线程的中断状态
//                e.printStackTrace();
//                break;  // 中断后跳出循环
//            } catch (Exception e) {
//                e.printStackTrace();  // 捕获可能的其他异常
//                break;
//            }
//
//        }
//        sender.accept(String.valueOf("[DONE]"));  // 每次发送一个字符

    }
    public ChatRecordReqDTO generateRecord(Long sessionId){//根据会话id生成一个聊天记录的DTO对象
        List<Object> jsonObjectList  = distributedCache.get(
                SESSION_MESSAGE_KEY + sessionId,//第一个参数是键值 能唯一的确定缓存中的数据
                List.class,
                ()-> chatMessageMapper.selectMessagesBySessionId(sessionId),
                7200 // 设置缓存的过期时间为7200秒（2小时）
                ,TimeUnit.SECONDS
        );
        List<ChatMessageDO> chatMessageDOS = jsonObjectList.stream()
                .map(obj -> {
                    if (obj instanceof com.alibaba.fastjson2.JSONObject) {
                        // 如果是 JSONObject，则将其转换为 ChatMessageDO
                        return ((com.alibaba.fastjson2.JSONObject) obj).toJavaObject(ChatMessageDO.class);
                    } else {
                        // 如果已经是 ChatMessageDO，直接返回
                        return (ChatMessageDO) obj;
                    }
                })
                .toList();
//        json转换
        List<ChatRecordReqDTO.Dialogue> allDialogues = chatMessageDOS.stream()
                .map(chatMessageDO -> ChatRecordReqDTO.Dialogue.builder()
                        .role("user".equals(chatMessageDO.getSenderType()) ? "client" : "assistant")
                        .content(chatMessageDO.getMessageText())
                        .build())
                .toList();  // 收集所有的 Dialogue 对象
// 创建一个 ChatRecordReqDTO 对象，将所有的 Dialogue 对象添加到其中
        ChatRecordReqDTO chatRecordReqDTO = ChatRecordReqDTO.builder()
                .dialogues(new ArrayList<>(allDialogues))  // 将所有的 Dialogue 添加到 List 中
                .build();
        return chatRecordReqDTO;

    }

    @Override
    public void uploadMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessageDO chatMessageDO = BeanUtil.convert(chatMessageDTO, ChatMessageDO.class);
        if(StrUtil.equals(chatMessageDTO.getSenderType(),"user")){
            chatMessageDO.setSenderId(UserContext.getUserId());
        }                                                        //                                          需要取出的类型
        ChatSessionDO existingSession =distributedCache.get(SESSION_KEY+chatMessageDO.getSessionId(),ChatSessionDO.class,new CacheLoader<ChatSessionDO>() {
            @Override
            public ChatSessionDO load() {
                return chatSessionMapper.selectById(chatMessageDO.getSessionId());
            }
        },7200, TimeUnit.SECONDS);
        //只是做了一个判空 没有传递任何值，若为空则此时redis和数据库中都没有该对象，用户传参错误
        Optional.ofNullable(existingSession)
                .orElseThrow(() -> new ClientException("sessionId异常"));

        //若existingSession有值，则直接插入数据库，mybatis-plus的insert会返回一个整数，成功为1，失败为0
        int insert = chatMessageMapper.insert(chatMessageDO);
        if (!SqlHelper.retBool(insert)) {
            throw new ServiceException("[%s] 插入聊天记录 失败");
        }
        distributedCache.delete(SESSION_MESSAGE_KEY + chatMessageDO.getSessionId());
    }

    @Override//java主要实现的是获取到chatRecordReqDTO来传给python接口
    public ReportRespDTO generateReport(Long sessionId) {
        ChatRecordReqDTO chatRecordReqDTO = generateRecord(sessionId);
        System.out.println(chatRecordReqDTO);
        return modelRemoteService.generateReport(chatRecordReqDTO);
    }

    @Override
    public List<UserLawyerRespDTO>  matchingLawyer(Long sessionId) {
        return  lawyerMapper.selectRandomLawyers();
    }

    @Override
    public PageResponse<ChatSessionRespDTO> getChatList(PageRequest pageRequest) {
        String userId=UserContext.getUserId();
        LambdaQueryWrapper<ChatSessionDO> queryWrapper;
        UserLawyerRespDTO userInfo = distributedCache.get(USER_INFO_KEY + userId, UserLawyerRespDTO.class, new CacheLoader<UserLawyerRespDTO>() {
                    @Override
                    public UserLawyerRespDTO load() {
                        // 从数据库加载用户信息
                        UserLawyerRespDTO userInfo = userMapper.getUserAndLawyerInfo(userId);
                        return userInfo;
                    }
                }, 1,
                TimeUnit.DAYS);

        if(StrUtil.equals(userInfo.getRole(),"lawyer")){
            queryWrapper = Wrappers.lambdaQuery(ChatSessionDO.class)
                    .eq(ChatSessionDO::getLawyerId, userId)
                    .ne(ChatSessionDO::getStatus, "ended")  // 排除 status 为 ended 的会话
                    .orderByDesc(ChatSessionDO::getCreateTime);
        }else{
            queryWrapper = Wrappers.lambdaQuery(ChatSessionDO.class)
                    .eq(ChatSessionDO::getUserId, userId)
                    .ne(ChatSessionDO::getStatus, "ended")  // 排除 status 为 ended 的会话
                    .orderByDesc(ChatSessionDO::getCreateTime);
        }




        IPage<ChatSessionDO> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        IPage<ChatSessionDO> resultPage = chatSessionMapper.selectPage(page, queryWrapper);
//        / 使用 convert 方法将 ChatSessionDO 转换为 ChatSessionRespDTO，并将结果收集为列表
        List<ChatSessionRespDTO> convertedRecords = resultPage.getRecords().stream()
                .map(chatSessionDO -> BeanUtil.convert(chatSessionDO, ChatSessionRespDTO.class))
                .collect(Collectors.toList());

        // 构建 PageResponse 对象并返回
        return new PageResponse<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), convertedRecords);

    }

    @Override
    public List<ChatMessageRespDTO> getChatDetail(Long sessionId) {
        // 假设 userId 是当前用户的ID
        String userId = UserContext.getUserId();  // 请替换成实际的用户ID变量

// 从分布式缓存获取消息列表
        String jsonMessages = distributedCache.get(
                SESSION_MESSAGE_KEY + sessionId,
                String.class,
                () -> {
                    List<ChatMessageDO> messages = chatMessageMapper.selectMessagesBySessionId(sessionId);
                    return JSON.toJSONString(messages);  // 将数据库查询结果序列化为 JSON 字符串存储
                },
                7200, TimeUnit.SECONDS
        );

        // 将 JSON 字符串安全地转换为 DTO 列表
        List<ChatMessageRespDTO> messages = JSON.parseObject(jsonMessages, new TypeReference<List<ChatMessageRespDTO>>(){});

// 增加 alignment 字段，根据 senderId 和 userId 比较结果
        messages.forEach(message -> {
            if (userId.equals(message.getSenderId())) {
                message.setAlignment(1);  // 设置 alignment 为 1，代表消息显示在右侧
            } else {
                message.setAlignment(0);  // 设置 alignment 为 0，代表消息显示在左侧
            }
        });
        return messages;
    }

    @Override
    public void sendToUser(String message, ConcurrentHashMap<String, ChatWithLawyerServer> chatWebSocketMap,String userId,String lawyerId) throws IOException {
        ChatWithLawyerServer receiver = chatWebSocketMap.get(lawyerId);
        Optional.ofNullable(receiver)
                .ifPresentOrElse(
                        r -> {
                            try {
                                r.sendMessage(message);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            log.info("消息已转发给用户 " + lawyerId);
                        },
                        () -> {
                            log.info("用户不在线，消息将存储为离线消息");
                            // TODO: 使用微信模板消息通知用户
                            Map<String, Map<String, String>> data = Map.of(
                                    "name", Map.of("value", "用户"),
                                    "message", Map.of("value", message),
                                    "date", Map.of("value", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")))
                            );
                            SubscribeMessageReqDTO  subscribeMessageReqDTO= SubscribeMessageReqDTO.builder()
                                    .toUser(lawyerId)
                                    .data(data)
                                    .lang(LANG_TYPE)
                                    .page(PAGE_KEY)
                                    .miniprogramState(MINIPRIGRAM_STATE)
                                    .templateId(TEMPLATE_ID)
                                    .build();
                            String accessToken = distributedCache.get(ACCESS_TOKEN, String.class, new CacheLoader<String>() {
                                @Override
                                public String load() {
                                    return chatRemoteService.getAccessToken(App_id, Secret_key).getAccessToken();
                                }
                            }, 7200, TimeUnit.SECONDS);

                            System.out.println(chatRemoteService.sendSubscribeMessage(accessToken,subscribeMessageReqDTO));

//                            Long sessionId = chatWebSocketMap.get(userId).getSessionId();
//                            ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
//                                    .senderType("user")
//                                    .senderId(userId)
//                                    .messageText(message)
//                                    .sessionId(sessionId)
//                                    .build();
//                            ChatServiceImpl.this.uploadMessage(chatMessageDTO);

                        }
                );



    }

    @Override
    public void sendTime(ConcurrentHashMap<String, ChatWithLawyerServer> chatWebSocketMap, String userId, Long sessionId) {
        Timer timer=new Timer();
        ChatWithLawyerServer receiver = chatWebSocketMap.get(userId);
        // 只有当连接存在时才启动推送
        Optional.ofNullable(receiver).ifPresentOrElse(r -> {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        // 获取当前时间
                        Date now = new Date();

                        // 获取会话的创建时间
                        ChatSessionDO existingSession = distributedCache.get(SESSION_KEY + sessionId, ChatSessionDO.class, new CacheLoader<ChatSessionDO>() {
                            @Override
                            public ChatSessionDO load() {
                                return chatSessionMapper.selectById(sessionId);
                            }
                        }, 7200, TimeUnit.SECONDS);

                        Date createTime = existingSession.getCreateTime();

                        // 计算会话已经持续了多长时间（毫秒）
                        long elapsedTimeMillis = now.getTime() - createTime.getTime();

                        // 12小时对应的毫秒数
                        long twelveHoursInMillis = TimeUnit.HOURS.toMillis(12);

                        // 计算剩余的时间
                        long remainingTimeMillis = twelveHoursInMillis - elapsedTimeMillis;

                        // 如果剩余时间小于等于0，停止推送
                        if (remainingTimeMillis <= 0) {
                            r.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "会话已经结束")); // 关闭 WebSocket 会话
                            timer.cancel(); // 停止定时器
                        } else {
                            // 将剩余的时间转换为小时和分钟
                            long remainingHours = TimeUnit.MILLISECONDS.toHours(remainingTimeMillis);
                            long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis) % 60;
                            try{
                                r.sendMessage(JSON.toJSONString("leftTime:"+remainingHours + "h" + remainingMinutes + "min"));
                            }catch (Exception  e){
                                timer.cancel(); // 停止计时器，防止重复调用
                            }
                            // 推送剩余的时间到客户端
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 30 * 1000); // 初始延迟为0，间隔30秒推送一次
        }, () -> {
            // receiver 为空的处理逻辑，可以取消计时器或返回
            timer.cancel(); // 停止计时器，防止重复调用
        });
    }
}


