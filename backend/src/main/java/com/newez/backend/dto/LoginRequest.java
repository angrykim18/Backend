package com.newez.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter               // ✅ 이 줄을 추가합니다.
@NoArgsConstructor      // ✅ 이 줄을 추가합니다.
public class LoginRequest {
    private String adminId;
    private String password;
}