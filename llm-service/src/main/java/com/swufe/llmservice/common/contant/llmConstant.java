package com.swufe.llmservice.common.contant;

public class llmConstant {

    public static final String PROJECT_CONTEXT_PATH = "/api/llm-service";

    public static final long FILE_INIT_STATUS = 1L; // 上传成功且未解析

    public static final int PAGE_SIZE = 5; // 每页展示五页数据

    public static String HEAD_IMG_DIR_PATH = null;
    public static String DOCX_FILE_DIR_PATH = null;
    public static String EXECUTED_DOCX_FILE_DIR_PATH = null;

    public static final int TIME_OUT_OF_SECONDS = 7200;
    public static final String LLM_FILE_KEY = "contract-review-llm-service-file-";
    public static final String LLM_STATUS_KEY = "contract-review-llm-service-status-";


}
