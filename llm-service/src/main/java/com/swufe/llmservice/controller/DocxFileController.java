package com.swufe.llmservice.controller;

import com.swufe.chatlaw.Results;
import com.swufe.chatlaw.page.PageRequest;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.chatlaw.result.Result;
import com.swufe.llmservice.dto.resp.DocxFileRespDTO;
import com.swufe.llmservice.service.DocxFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.swufe.llmservice.common.contant.llmConstant.PROJECT_CONTEXT_PATH;

@RequestMapping(PROJECT_CONTEXT_PATH)
@RequiredArgsConstructor
@RestController
public class DocxFileController {

    private final DocxFileService docxFileService;

    /**
     * 处理上传合同文件
     * @param docxFile
     * @return
     */
    @PostMapping("/upload")  // ok
    public Result<Void> uploadDocxFile(@RequestParam("file") MultipartFile docxFile) {
        docxFileService.uploadDocxFile(docxFile);
        return Results.success();
    }

    /**
     * 根据合同id查询合同
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")  // ok
    public Result<Void> deleteDocxFile(@PathVariable("id") Long id) {
        docxFileService.deleteDocxFile(id);
        return Results.success();
    }

    //todo update应该放在哪里？

    /**
     * 根据合同id删除文件
     * @param id
     * @return
     */
    @GetMapping("/retrieval/{id}")
    public Result<DocxFileRespDTO> retrievalDocxFile(@PathVariable("id") Long id) {
        return Results.success(docxFileService.retrievalDocxFile(id));
    }


    /**
     * 分页查询合同
     * @param pageRequest
     * @return
     */
    @GetMapping("/retrieval")
    public Result<PageResponse<DocxFileRespDTO>> retrievalDocxFilesByPage(PageRequest pageRequest) {
        return Results.success(docxFileService.retrievalDocxFilesByPage(pageRequest));
    }
}
