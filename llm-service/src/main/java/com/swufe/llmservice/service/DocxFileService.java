package com.swufe.llmservice.service;


import com.swufe.llmservice.dto.DocxFileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocxFileService {

    void uploadDocxFile(MultipartFile docxFile);

    void deleteDocxFile(Long id);

    //todo

    DocxFileDTO retrievalDocxFile(Long id);

    List<DocxFileDTO> retrievalDocxFiles();
}
