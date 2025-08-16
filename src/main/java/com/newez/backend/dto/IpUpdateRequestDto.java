package com.newez.backend.dto; // DTO는 별도 패키지로 관리하는 것이 좋습니다.

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 기본 생성자 추가
public class IpUpdateRequestDto {
    private String ip;
}