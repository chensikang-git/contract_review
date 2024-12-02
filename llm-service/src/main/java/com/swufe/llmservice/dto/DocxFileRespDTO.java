package com.swufe.llmservice.dto;


import com.swufe.llmservice.dao.enetty.FileStatusDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocxFileRespDTO implements Serializable {

    private Long id;

    private String fileName;

    private FileStatusDO fileStatus;
}
