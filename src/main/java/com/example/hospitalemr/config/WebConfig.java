package com.example.hospitalemr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// WebConfig.java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")   // application.properties 에 uploadDir 설정
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/uploads/**" 요청 → 파일 시스템 "uploadDir/" 로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600)  // (선택) 캐시 설정
                .resourceChain(true);
    }
}

