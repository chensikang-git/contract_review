package com.swufe.llmservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocxFileRespDTO{

    private String fileName;

    private Date createTime;

    private DocxFileStatusRespDTO fileStatus;
}
