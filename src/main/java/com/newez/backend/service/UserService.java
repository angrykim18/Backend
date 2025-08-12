package com.newez.backend.service;

import com.newez.backend.domain.User;
import com.newez.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class UserService {

    private final UserRepository userRepository;
    private final GeoIpService geoIpService;

    // ... 다른 서비스 메소드들 ...


    // ▼▼▼ 여기에 새 서비스 메소드를 추가합니다 ▼▼▼
    @Transactional
    public void updateUserIpByDeviceId(String deviceId, String ip) {
        // 1. 사용자 조회
        User user = userRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id=" + deviceId));

        // 2. 국가 정보 조회 (실제 구현 필요)
        String country = geoIpService.getCountry(ip);

        // 3. 사용자 정보 업데이트
        user.setIp(ip);
        user.setCountry(country);

        // @Transactional에 의해 메소드 종료 시 변경된 내용을 DB에 자동 반영 (Dirty Checking)
    }


}