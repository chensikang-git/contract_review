package com.swufe.llmservice.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.swufe.llmservice.dao.enetty.FileStatusDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocxFileDTO {

    private Long id;

    private String fileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date uploadTime;

    private FileStatusDO fileStatus;

}
