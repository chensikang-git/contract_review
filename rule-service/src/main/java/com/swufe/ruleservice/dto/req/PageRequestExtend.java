package com.swufe.ruleservice.dto.req;


import lombok.Data;

@Data
public class PageRequestExtend {

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页显示条数
     */
    private Long size = 10L;

    /**
     * 进行模糊匹配的字符串
     */
    private String words;
}
