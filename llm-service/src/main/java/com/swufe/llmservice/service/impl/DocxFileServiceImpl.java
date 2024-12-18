package com.swufe.llmservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.swufe.chatlaw.DistributedCache;
import com.swufe.chatlaw.core.UserContext;
import com.swufe.chatlaw.exception.ClientException;
import com.swufe.chatlaw.exception.ServiceException;
import com.swufe.chatlaw.page.PageRequest;
import com.swufe.chatlaw.page.PageResponse;
import com.swufe.llmservice.dao.enetty.DocxFileDO;
import com.swufe.llmservice.dao.enetty.DocxFileStatusDO;
import com.swufe.llmservice.dao.mapper.DocxFileMapper;
import com.swufe.llmservice.dao.mapper.DocxFileStatusMapper;
import com.swufe.llmservice.dto.resp.DocxFileRespDTO;
import com.swufe.llmservice.service.DocxFileService;
import com.swufe.llmservice.tooklit.SBeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.swufe.chatlaw.errorcode.BaseErrorCode.*;
import static com.swufe.llmservice.common.contant.llmConstant.*;


@Service
@RequiredArgsConstructor
public class DocxFileServiceImpl implements DocxFileService {

    private final DocxFileMapper docxFileMapper;
    private final DocxFileStatusMapper docxFileStatusMapper;

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
        DocxFileDO docxFileDO = DocxFileDO.builder()
                .fileName(docxFileName)
                .filePath(docxFilePath)
                .status(FILE_INIT_STATUS)
                .userId(userId)
                .build();
        int insert = docxFileMapper.insert(docxFileDO);

        if (!SqlHelper.retBool(insert)) {
            throw new ServiceException(FILE_ADD_ERROR);
        }
    }

    @Override
    public void deleteDocxFile(Long id) {
        // 删数据
        int delete = docxFileMapper.delete(
                new LambdaQueryWrapper<DocxFileDO>()
                        .eq(DocxFileDO::getId, id)
                        .eq(DocxFileDO::getUserId, UserContext.getUserId())
        );
        if (!SqlHelper.retBool(delete)) {
            throw new ServiceException(FILE_NOT_FOUND_ERROR);
        }

        //删除缓存
        distributedCache.delete(LLM_STATUS_KEY + id);
    }


    @Override
    public DocxFileRespDTO retrievalDocxFile(Long id) {
        DocxFileDO docxFileDO = distributedCache.get(
                LLM_FILE_KEY + id,
                DocxFileDO.class,
                () -> docxFileMapper.selectOne(new LambdaQueryWrapper<DocxFileDO>()
                        .eq(DocxFileDO::getId, id)
                        .eq(DocxFileDO::getUserId, UserContext.getUserId())),
                TIME_OUT_OF_SECONDS,
                TimeUnit.SECONDS
        );
        if (docxFileDO == null) {
            throw new ServiceException(FILE_NOT_FOUND_ERROR);
        }
        DocxFileStatusDO docxFileStatusDO = distributedCache.get(
                LLM_STATUS_KEY + docxFileDO.getStatus(),
                DocxFileStatusDO.class,
                () -> docxFileStatusMapper.selectOne(
                        new LambdaQueryWrapper<DocxFileStatusDO>().eq(DocxFileStatusDO::getId, docxFileDO.getStatus())
                ),
                TIME_OUT_OF_SECONDS,
                TimeUnit.SECONDS
        );
        docxFileDO.setFileStatus(docxFileStatusDO);
        System.out.println(docxFileDO);

        DocxFileRespDTO docxFileRespDTO = new DocxFileRespDTO();
        SBeanUtil.superCopy(docxFileDO, docxFileRespDTO); //
        return docxFileRespDTO;
    }


    @Override
    public PageResponse<DocxFileRespDTO> retrievalDocxFilesByPage(PageRequest pageRequest) {
        IPage<DocxFileDO> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());

        LambdaQueryWrapper<DocxFileDO> queryWrapper =
                new LambdaQueryWrapper<DocxFileDO>()
                        .eq(DocxFileDO::getUserId, UserContext.getUserId());
        IPage<DocxFileDO> resultPage = docxFileMapper.selectPage(page, queryWrapper);
        List<DocxFileRespDTO> convertedRecords = resultPage.getRecords()
                .stream()
                .map(docxFileDO -> {
                    docxFileDO.setFileStatus(
                            distributedCache.get(
                                    LLM_STATUS_KEY + docxFileDO.getStatus(),
                                    DocxFileStatusDO.class,
                                    () -> docxFileStatusMapper.selectOne(
                                            new LambdaQueryWrapper<DocxFileStatusDO>().eq(DocxFileStatusDO::getId, docxFileDO.getStatus())
                                    ),
                                    TIME_OUT_OF_SECONDS,
                                    TimeUnit.SECONDS
                            )
                    );
                    return docxFileDO;
                })
                .map(docxFileDO -> {
                    DocxFileRespDTO docxFileRespDTO = new DocxFileRespDTO();
                    BeanUtils.copyProperties(docxFileDO, docxFileRespDTO);
                    return docxFileRespDTO;
                })
                .collect(Collectors.toList());
        return new PageResponse<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), convertedRecords);
    }
}
