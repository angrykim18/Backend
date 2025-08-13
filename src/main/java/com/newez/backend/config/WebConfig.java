package com.newez.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${poster.upload-dir}")
    private String uploadDir; // application.properties에 설정된 C:/img/ 경로

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ [수정] /posters/ 라는 URL 경로로 요청이 오면,
        // 실제 컴퓨터의 C:/img/ 폴더에서 파일을 찾아 보여주도록 설정합니다.
        registry.addResourceHandler("/posters/**")
                .addResourceLocations("file:" + uploadDir);
    }
}