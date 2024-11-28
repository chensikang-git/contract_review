package com.swufe.chatservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Timer;

@Configuration
public class TimerConfig {

    @Bean
    public Timer timer() {
        return new Timer();
    }
}
