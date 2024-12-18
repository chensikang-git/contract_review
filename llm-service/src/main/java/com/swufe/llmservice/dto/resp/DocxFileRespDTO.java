package com.swufe.llmservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocxFileRespDTO{

    private Long id;

    private String fileName;

    private Date createTime;

    private DocxFileStatusRespDTO fileStatus;
}
