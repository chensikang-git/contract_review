package com.swufe.chatservice.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        // 设置为FULL输出所有Feign调用的日志信息
        return Logger.Level.FULL;
    }
    
    @Bean
    public Logger feignLogger() {
        // 使用自定义的Logger实现
        return new FeignLogger();
    }

    public static class FeignLogger extends Logger {
        @Override
        protected void log(String configKey, String format, Object... args) {
            // 在这里实现自定义的日志记录逻辑
            System.out.println(String.format(methodTag(configKey) + format, args));
        }
    }
}
