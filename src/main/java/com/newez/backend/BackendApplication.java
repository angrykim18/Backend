package com.newez.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // ✅ [추가] import

@EnableJpaAuditing // ✅ [추가] JPA Auditing(자동 시간 기록) 기능 활성화
@SpringBootApplication
@EntityScan("com.newez.backend.domain")
public class BackendApplication {

	public static void main(String[] args) {

		System.setProperty("spring.datasource.hikari.connection-timeout", "20000");
		System.setProperty("server.port", "8081");
		System.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/db_newez?serverTimezone=Asia/Seoul");
		System.setProperty("spring.datasource.username", "root");
		System.setProperty("spring.datasource.password", "khbkhs1004!"); // ❗️비밀번호 수정



		SpringApplication.run(BackendApplication.class, args);

	}

}