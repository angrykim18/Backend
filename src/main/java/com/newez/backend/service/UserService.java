package com.newez.backend.service;

import com.newez.backend.domain.User;
import com.newez.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class UserService {

    private final UserRepository userRepository;
    private final GeoIpService geoIpService;


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

    }

    }




