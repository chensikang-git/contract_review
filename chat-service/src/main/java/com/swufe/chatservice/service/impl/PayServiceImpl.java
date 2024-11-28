package com.swufe.chatservice.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.swufe.chatlaw.DistributedCache;
import com.swufe.chatlaw.core.CacheLoader;
import com.swufe.chatlaw.core.UserContext;
import com.swufe.chatlaw.exception.ClientException;
import com.swufe.chatlaw.exception.ServiceException;
import com.swufe.chatlaw.toolkit.BeanUtil;
import com.swufe.chatservice.dao.entity.ChatSessionDO;
import com.swufe.chatservice.dao.entity.OrderDO;
import com.swufe.chatservice.dao.mapper.ChatSessionMapper;
import com.swufe.chatservice.dao.mapper.OrderMapper;
import com.swufe.chatservice.dto.req.PayDTO;
import com.swufe.chatservice.dto.resp.AgreeRespDTO;
import com.swufe.chatservice.dto.resp.PreOrderRespDTO;
import com.swufe.chatservice.service.PayService;
import com.swufe.chatservice.toolkit.PayIdGeneratorManager;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.exception.ParseException;
import com.wechat.pay.contrib.apache.httpclient.exception.ValidationException;
import org.junit.Assert;


import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;

import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.swufe.chatlaw.constant.MiniProgramConstant.*;
import static com.swufe.chatservice.common.constant.RedisKeyConstant.SESSION_KEY;
import static com.swufe.chatservice.common.constant.RedisKeyConstant.SESSION_MESSAGE_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayServiceImpl implements PayService {
    private final OrderMapper orderMapper;
    private final DistributedCache distributedCache;
    private final ChatSessionMapper chatSessionMapper;
//    private final PayRemoteService payRemoteService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PrepayWithRequestPaymentResponse pay(PayDTO payDTO) throws IOException {
        String userId = UserContext.getUserId();
        String paySn = PayIdGeneratorManager.generateId(userId);
        // 在这里生成签名，并确保使用相同的nonceStr和timestamp
        OrderDO orderDO = OrderDO.builder()
                .orderId(paySn)
                .openid(userId)
                .sessionId(payDTO.getSessionId())
                .orderStatus("pending")
                .orderAmount(1)
                .build();
        int insert = orderMapper.insert(orderDO);
        if (!SqlHelper.retBool(insert)) {
            throw new ServiceException(String.format("发起订单失败"));
        }


//        WeChatPayReqDTO weChatPayReqDTO = WeChatPayReqDTO.builder()
//                .outTradeNo(paySn)
//                .amount(WeChatPayReqDTO.Amount.builder().total(AMOUNT_TOTAL).currency("CNY").build())  // 内联创建 Amount 对象
//                .appid(App_id)
//                .mchid(MCHID_KEY)
//                .payer(WeChatPayReqDTO.Payer.builder().openid(userId).build())  // 内联创建 Payer 对象
//                .description(DESCRIBE_KEY)
//                .notifyUrl(NOTIFY_URL_KEY)
//                .build();
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(1);
        amount.setCurrency("CNY");
        request.setAmount(amount);
        request.setAppid(App_id);
        request.setMchid(MCHID_KEY);
        request.setDescription(DESCRIBE_KEY);
        request.setNotifyUrl(NOTIFY_URL_KEY);
        request.setOutTradeNo(paySn);
        Payer payer = new Payer();
        payer.setOpenid(userId);
        request.setPayer(payer);
        ClassPathResource resource = new ClassPathResource("apiclient_key.pem");
        String s = new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);

        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .privateKey(s)
                        .merchantId(MCHID_KEY)
                        .merchantSerialNumber(MCH_SERIAL_NO)
                        .apiV3Key(API_V3_KEY)
                        .build();

        // 构建service
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(config).build();
        PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);


        return response;
    }

    @Override
    public Void changeAgree(Long sessionId) {
       String userId=UserContext.getUserId();
        ChatSessionDO existingSession =distributedCache.get(SESSION_KEY+sessionId, ChatSessionDO.class,new CacheLoader<ChatSessionDO>() {
            @Override
            public ChatSessionDO load() {
                return chatSessionMapper.selectById(sessionId);
            }
        },7200, TimeUnit.SECONDS);
        // 判断existingSession是否为空
        Optional.ofNullable(existingSession).ifPresentOrElse(session -> {
            // 获取 existingSession 中的 lawyerId 和 userId
            String lawyerId = session.getLawyerId();

            // 如果 lawyerId 和当前用户ID相同，则更新 lawyerAgree
            if (StrUtil.equals(lawyerId,userId)) {
                session.setLawyerAgree(true);  // 更新律师同意状态
            }
            // 如果 userId 和当前用户ID相同，则更新 userAgree
            else if (StrUtil.equals(session.getUserId(),userId)) {
                session.setUserAgree(true);  // 更新用户同意状态
                session.setStatus("ended");
            }

            // 将更新后的会话存入数据库
            chatSessionMapper.updateById(session);

        }, () -> {
            // 如果existingSession为null，可以在这里处理空值的情况
            throw new ClientException("sessionId 错误");
        });
        distributedCache.delete(SESSION_KEY+sessionId);

        return null;
    }

    @Override
    public AgreeRespDTO getAgree(Long sessionId) {

        ChatSessionDO existingSession =distributedCache.get(SESSION_KEY+sessionId, ChatSessionDO.class,new CacheLoader<ChatSessionDO>() {
            @Override
            public ChatSessionDO load() {
                return chatSessionMapper.selectById(sessionId);
            }
        },7200, TimeUnit.SECONDS);
        AgreeRespDTO agreeResp = Optional.ofNullable(existingSession)
                .map(session -> {
                    AgreeRespDTO agreeRespDTO = BeanUtil.convert(session, AgreeRespDTO.class);
                    // 判断 lawyerAgree 和 userAgree
                    if (agreeRespDTO.isLawyerAgree() && agreeRespDTO.isUserAgree()) {
                        // 如果 lawyerAgree 和 userAgree 都是 true，这里可以执行一些逻辑
                        agreeRespDTO.setLawyerPay(true);
                    } else {
                        agreeRespDTO.setLawyerPay(false);
                    }
                    return agreeRespDTO;
                })
                .orElseThrow(() -> new ClientException("sessionId 错误"));
        return agreeResp;


    }

//    @Override
//    public void payNotify(NotificationRequest notificationRequest) throws ValidationException, ParseException {
//
//
//        // 验签和解析请求体
//        Assert.assertNotNull(notification);
//    }

}
