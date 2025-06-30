package com.eaglebank.config;

import com.eaglebank.util.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public SecurityUtil securityUtil() {
        return new SecurityUtil();
    }
}
