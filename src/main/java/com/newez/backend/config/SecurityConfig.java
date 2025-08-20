package com.newez.backend.config; // 본인 프로젝트에 맞는 패키지 경로인지 확인하세요.

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정을 적용합니다.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 보호 기능은 비활성화합니다. (API 서버에서는 비활성화가 일반적)
                .csrf(csrf -> csrf.disable())

                // 세션은 사용하지 않습니다. (JWT 같은 토큰 방식 사용 시)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 모든 요청을 일단 허용하여 통신 문제부터 해결합니다.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 프론트엔드 주소를 직접 적어줍니다.
        // 로컬 개발 환경과 실제 서비스 환경 주소를 모두 적어두면 편리합니다.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://www.abcd1234.net"));

        // 허용할 HTTP 메서드(GET, POST, PUT, DELETE 등)를 지정합니다.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 모든 헤더를 허용합니다.
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 자격 증명(쿠키 등)을 허용합니다.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 대해 위에서 정의한 CORS 정책을 적용합니다.
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}