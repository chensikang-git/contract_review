package com.swufe.chatservice.service.sse;

import com.swufe.chatservice.dto.resp.AiAnswerRespDTO;
import com.swufe.chatservice.remote.req.ChatRecordReqDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import com.swufe.chatservice.dto.req.AiAnswerReqDTO;

import static com.swufe.chatservice.common.constant.ChatConstant.MODEL_URL;

@Service
public class SseService {

    private final WebClient webClient;

    public SseService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(MODEL_URL).build();
    }

    public Flux<String> streamProblem(AiAnswerReqDTO aiAnswerReqDTO) {
        return webClient.post()
                .uri("/generate_problem")
                .bodyValue(aiAnswerReqDTO) // 传递请求数据
                .retrieve()
                .bodyToFlux(String.class);  // 使用 Flux 来处理 SSE 流数据
    }
}
