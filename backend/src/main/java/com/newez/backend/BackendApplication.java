package com.newez.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

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
