package com.swufe.llmservice.dto.resp;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocxFileStatusRespDTO {

    private String statusName;

    private String statusDesc;
}
