package com.swufe.llmservice.service;


import com.swufe.llmservice.dto.DocxFileRespDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocxFileService {

    void uploadDocxFile(MultipartFile docxFile);

    void deleteDocxFile(Long id);

    //todo

    DocxFileRespDTO retrievalDocxFile(Long id);

    List<DocxFileRespDTO> retrievalDocxFiles();
}
