package com.newez.backend.controller;

import com.newez.backend.domain.Admin;
import com.newez.backend.dto.LoginRequest;
import com.newez.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${cors.allowed-origins}") // ✅ 이 줄을 추가하여 에러를 해결합니다.
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Optional<Admin> adminOptional = adminRepository.findByAdminId(loginRequest.getAdminId());

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            if (admin.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok("로그인 성공");
            }
        }
        return ResponseEntity.status(401).body("아이디 또는 비밀번호가 틀립니다");
    }
}