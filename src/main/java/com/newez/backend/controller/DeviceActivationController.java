package com.newez.backend.controller;

import com.newez.backend.domain.Admin;
import com.newez.backend.domain.Log;
import com.newez.backend.domain.User;
import com.newez.backend.dto.HqActivationRequest;
import com.newez.backend.repository.AdminRepository;
import com.newez.backend.repository.LogRepository;
import com.newez.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class DeviceActivationController {


    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final LogRepository logRepository;


    @PostMapping("/activate-device")
    public ResponseEntity<?> activateDeviceByAdmin(@RequestBody HqActivationRequest request) {

        List<Admin> allAdmins = adminRepository.findAll();
        if (allAdmins.isEmpty()) {
            return ResponseEntity.status(500).body("{\"message\": \"서버에 설정된 관리자가 없습니다.\"}");
        }
        boolean passwordMatch = allAdmins.stream()
                .anyMatch(admin -> admin.getPassword().equals(request.getAdminPassword()));
        if (!passwordMatch) {
            return ResponseEntity.status(401).body("{\"message\": \"관리자 비밀번호가 일치하지 않습니다.\"}");
        }

        if (userRepository.findByDeviceId(request.getDeviceId()).isPresent()) {
            return ResponseEntity.status(409).body("{\"message\": \"이미 등록된 기기입니다.\"}");
        }

        User newUser = new User();
        newUser.setDeviceId(request.getDeviceId());
        String managementId = String.format("%05d", new Random().nextInt(100000));
        newUser.setManagementId(managementId);
        newUser.setName("신규고객-" + managementId);
        newUser.setStatus("사용중");
        newUser.setAdultContentAllowed(true);
        newUser.setSubscriptionEndDate(LocalDate.now().plusDays(1));
        newUser.setCreatedAt(new Date());
        User savedUser = userRepository.save(newUser);

        Log log = new Log();
        log.setLogType("본사 신규");
        String logMessage = String.format("'본사 관리자'가 새로운 기기(%s)를 개통했습니다.", request.getDeviceId());
        log.setLogMessage(logMessage);
        log.setExecutorId("본사 관리자");
        logRepository.save(log);

        return ResponseEntity.ok(savedUser);
    }
}