package com.swufe.llmservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.swufe.chatlaw.DistributedCache;
import com.swufe.chatlaw.core.UserContext;
import com.swufe.chatlaw.exception.ClientException;
import com.swufe.chatlaw.exception.ServiceException;
import com.swufe.llmservice.dao.enetty.DocxFileDO;
import com.swufe.llmservice.dao.mapper.DocxFileMapper;
import com.swufe.llmservice.dto.DocxFileDTO;
import com.swufe.llmservice.service.DocxFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.swufe.chatlaw.errorcode.BaseErrorCode.*;
import static com.swufe.llmservice.common.contant.llmConstant.*;


@Service
@RequiredArgsConstructor
public class DocxFileServiceImpl implements DocxFileService {

    private final DocxFileMapper docxFileMapper;
    private final DistributedCache distributedCache;

    @Override
    public void uploadDocxFile(MultipartFile docxFile) {
        String originalFilename = docxFile.getOriginalFilename();
        if (!originalFilename.substring(originalFilename.lastIndexOf(".") + 1).equals("docx")) {
            throw new ClientException(FILE_TYPE_ERROR);
        }
        Long userId = UserContext.getUserId();
        long timeStamp = System.currentTimeMillis();
        String docxFileName = userId + "_" + timeStamp + "_" + originalFilename;
        String docxFilePath = DOCX_FILE_DIR_PATH + File.separator + docxFileName;
        try {
            docxFile.transferTo(new File(docxFilePath));
        } catch (IOException e) {
            throw new ServiceException(FILE_TRANS_ERROR);
        }
        DocxFileDO docxFileDO = DocxFileDO.builder().fileName(docxFileName).filePath(docxFilePath).status(0).userId(userId).build();
        int insert = docxFileMapper.insert(docxFileDO);

        if (!SqlHelper.retBool(insert)) {
            throw new ServiceException(FILE_ADD_ERROR);
        }
    }

    @Override
    public void deleteDocxFile(Long id) {
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("id", id);
        idMap.put("user_id", UserContext.getUserId());
        int delete = docxFileMapper.deleteByMap(idMap);

        if (!SqlHelper.retBool(delete)) {
            throw new ServiceException(FILE_NOT_FOUND_ERROR);
        }
    }

    @Override
    public DocxFileDTO retrievalDocxFile(Long id) {
//        DocxFileDO docxFileDO = docxFileMapper.selectOne(
//                new QueryWrapper<DocxFileDO>()
//                        .eq("id", id).
//                        eq("user_id", UserContext.getUserId())
//        );
        DocxFileDO docxFileDO = distributedCache.get(
                LLM_FILE_KEY + id,
                DocxFileDO.class,
                () -> docxFileMapper.selectOne(
                        new QueryWrapper<DocxFileDO>()
                                .eq("id", id).
                                eq("user_id", UserContext.getUserId())
                ),
                TIME_OUT_OF_SECONDS,
                TimeUnit.SECONDS
        );
        if (docxFileDO == null) {
            throw new ServiceException(FILE_NOT_FOUND_ERROR);
        }
        DocxFileDTO docxFileDTO = new DocxFileDTO();
        BeanUtils.copyProperties(docxFileDO, docxFileDTO);
        return docxFileDTO;
    }


    @Override
    public List<DocxFileDTO> retrievalDocxFiles() {
        return docxFileMapper.selectList(
                        new QueryWrapper<DocxFileDO>().eq("user_id", UserContext.getUserId())
                )
                .stream()
                .map(docxFileDO -> {
                    DocxFileDTO docxFileDTO = new DocxFileDTO();
                    BeanUtils.copyProperties(docxFileDO, docxFileDTO);
                    return docxFileDTO;
                })
                .collect(Collectors.toList());
    }
}
