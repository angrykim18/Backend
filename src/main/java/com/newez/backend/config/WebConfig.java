package com.newez.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application.properties에 설정된 C:/img/ 경로
    @Value("${poster.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 포스터 이미지 경로 설정: /posters/** 요청을 C:/img/ 폴더와 연결
        registry.addResourceHandler("/posters/**")
                .addResourceLocations("file:" + uploadDir);

        // 2. 앱 업데이트 파일 경로 설정: /downloads/** 요청을 C:/updateapp/ 폴더와 연결
        registry.addResourceHandler("/downloads/**")
                .addResourceLocations("file:///C:/updateapp/");
    }
}