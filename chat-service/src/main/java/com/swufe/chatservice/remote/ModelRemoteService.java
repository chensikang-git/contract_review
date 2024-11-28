package com.swufe.chatservice.remote;

import com.swufe.chatservice.dto.resp.ReportRespDTO;
import com.swufe.chatservice.dto.resp.TenQuestionsRespDTO;
import com.swufe.chatservice.remote.dto.CaseTypeRespDTO;
import com.swufe.chatservice.remote.req.ChatRecordReqDTO;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

import static com.swufe.chatservice.common.constant.ChatConstant.MODEL_URL;

@FeignClient(name = "ModelChatService", url = MODEL_URL)
public interface ModelRemoteService {

    @PostMapping("/generate_ten_questions")
    TenQuestionsRespDTO generateTenQuestions(@RequestBody ChatRecordReqDTO chatRecordReqDTO);
//
//    @PostMapping("/generate_problem")
//    ApiResponse generateProblem(@RequestBody RequestData requestData);
//
    @PostMapping("/analyze_case_type")
    CaseTypeRespDTO analyzeCaseType(@RequestBody ChatRecordReqDTO chatRecordReqDTO);
//
    @PostMapping("/generate_report")
    ReportRespDTO generateReport(@RequestBody ChatRecordReqDTO chatRecordReqDTO);
}
