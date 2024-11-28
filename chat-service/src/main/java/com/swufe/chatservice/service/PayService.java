package com.swufe.chatservice.service;

import com.swufe.chatservice.dto.req.PayDTO;
import com.swufe.chatservice.dto.resp.AgreeRespDTO;
import com.swufe.chatservice.dto.resp.PreOrderRespDTO;
import com.wechat.pay.contrib.apache.httpclient.exception.ParseException;
import com.wechat.pay.contrib.apache.httpclient.exception.ValidationException;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public interface PayService {
    PrepayWithRequestPaymentResponse pay(PayDTO payDTO) throws IOException;

    Void changeAgree(Long sessionId);

    AgreeRespDTO getAgree(Long sessionId);

//    void payNotify(NotificationRequest notificationRequest) throws ValidationException, ParseException;
}
