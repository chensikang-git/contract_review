package com.swufe.chatservice.controller;

import com.swufe.chatlaw.Results;
import com.swufe.chatlaw.result.Result;
import com.swufe.chatservice.dto.req.ChatMessageDTO;
import com.swufe.chatservice.dto.req.PayDTO;
import com.swufe.chatservice.dto.req.PaymentEventDTO;
import com.swufe.chatservice.dto.resp.AgreeRespDTO;
import com.swufe.chatservice.dto.resp.PreOrderRespDTO;
import com.swufe.chatservice.service.PayService;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

/**
 * 用户支付控制层
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatPayController {
    private final PayService payService;

    @PostMapping("/api/chat-service/place-order")
    public Result<PrepayWithRequestPaymentResponse> pay(@RequestBody PayDTO payDTO) throws IOException {
        return Results.success(payService.pay(payDTO));
    }

    @PostMapping("/wechat/notify")
    public Result<Void> payNotify(@RequestBody NotificationRequest  notificationRequest)  {
//        payService.payNotify(notificationRequest);
        return Results.success();
    }

    @PutMapping("/api/chat-service/agree-status")
    public Result<Void> changeAgree(@RequestBody Map<String, Long> requestBody)  {
        Long sessionId = requestBody.get("sessionId");
        return Results.success(payService.changeAgree(sessionId));
    }

    @GetMapping("/api/chat-service/agree-status")
    public Result<AgreeRespDTO> getAgree(@RequestParam(name = "sessionId", required = true) Long sessionId)  {
        return Results.success(payService.getAgree(sessionId));
    }
}
