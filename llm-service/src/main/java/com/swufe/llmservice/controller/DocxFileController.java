package com.swufe.llmservice.controller;

import com.swufe.chatlaw.Results;
import com.swufe.chatlaw.result.Result;
import com.swufe.llmservice.dto.DocxFileDTO;
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
    /**
     * 第一版写法
     */

    private final DocxFileService docxFileService;

    @PostMapping("/upload")
    public Result<Void> uploadDocxFile(@RequestParam("file") MultipartFile docxFile) {
        docxFileService.uploadDocxFile(docxFile);
        return Results.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteDocxFile(@PathVariable("id") Long id) {
        docxFileService.deleteDocxFile(id);
        return Results.success();
    }

    //todo update应该放在哪里？

    @GetMapping("/retrieval/{id}")
    public Result<DocxFileDTO> retrievalDocxFile(@PathVariable("id") Long id) {
        return Results.success(docxFileService.retrievalDocxFile(id));
    }

    @GetMapping("/retrieval")
    public Result<List<DocxFileDTO>> retrievalDocxFiles() {
        return Results.success(docxFileService.retrievalDocxFiles());
    }
}
