package com.swufe.llmservice.service;


import com.swufe.chatlaw.page.PageRequest;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.llmservice.dto.resp.DocxFileRespDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocxFileService {

    void uploadDocxFile(MultipartFile docxFile);

    void deleteDocxFile(Long id);

    //todo

    DocxFileRespDTO retrievalDocxFile(Long id);


    PageResponse<DocxFileRespDTO> retrievalDocxFilesByPage(PageRequest pageRequest);
}
