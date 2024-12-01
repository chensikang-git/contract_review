package com.swufe.llmservice;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.swufe.llmservice.dao.mapper")
@EnableFeignClients("com.swufe.llmservice.remote")
@EnableFileStorage
public class LlmServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmServiceApplication.class, args);
    }

}
