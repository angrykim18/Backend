package com.newez.backend.controller;

import com.newez.backend.domain.Agency;
import com.newez.backend.domain.Log;
import com.newez.backend.domain.User;
import com.newez.backend.dto.ActivationResponse;
import com.newez.backend.repository.AgencyRepository;
import com.newez.backend.repository.LogRepository;
import com.newez.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URLDecoder; // ✅ import 추가
import java.nio.charset.StandardCharsets; // ✅ import 추가
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private LogRepository logRepository;

    @GetMapping
    public Page<User> getAllUsers(
            @PageableDefault(size = 50, sort = "id") Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String keyword) {
        return userRepository.findByKeyword(keyword, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(userDetails.getName());
                    existingUser.setMemo(userDetails.getMemo());
                    existingUser.setSubscriptionEndDate(userDetails.getSubscriptionEndDate());
                    existingUser.setStatus(userDetails.getStatus());
                    existingUser.setAdultContentAllowed(userDetails.isAdultContentAllowed());
                    existingUser.setUserGroup(userDetails.getUserGroup());
                    User updatedUser = userRepository.save(existingUser);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<User> getUserByDeviceId(@PathVariable String deviceId) {
        try {
            // ✅ [핵심 수정] 전달받은 deviceId를 URL 디코딩하여 원본 문자열로 복원합니다.
            String decodedDeviceId = URLDecoder.decode(deviceId, StandardCharsets.UTF_8.toString());
            System.out.println(">>> [디코딩 후] deviceId: " + decodedDeviceId);

            return userRepository.findByDeviceId(decodedDeviceId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            System.out.println("[오류] deviceId 디코딩 실패: " + deviceId);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateDevice(@RequestBody Map<String, String> activationRequest) {
        String deviceId = activationRequest.get("deviceId");
        String agencyLoginId = activationRequest.get("agencyLoginId");
        String agencyPassword = activationRequest.get("agencyPassword");

        Optional<Agency> agencyOptional = agencyRepository.findByLoginId(agencyLoginId);

        if (agencyOptional.isEmpty()) {
            return ResponseEntity.status(404).body(new ActivationResponse("존재하지 않는 대리점 아이디입니다."));
        }

        Agency agency = agencyOptional.get();
        if (!agency.getPassword().equals(agencyPassword)) {
            return ResponseEntity.status(401).body(new ActivationResponse("대리점 비밀번호가 틀립니다."));
        }

        if (userRepository.findByDeviceId(deviceId).isPresent()) {
            return ResponseEntity.status(409).body(new ActivationResponse("이미 등록된 기기입니다."));
        }

        User newUser = new User();
        newUser.setDeviceId(deviceId);
        String managementId = String.format("%05d", new Random().nextInt(100000));
        newUser.setManagementId(managementId);
        newUser.setName("신규고객-" + managementId);
        newUser.setStatus("사용중");
        newUser.setSubscriptionEndDate(new java.sql.Date(System.currentTimeMillis()));
        User savedUser = userRepository.save(newUser);

        Log log = new Log();
        log.setLogType("신규 개통");
        String logMessage = String.format("'%s' 대리점이 새로운 기기(%s)를 개통했습니다.", agency.getAgencyName(), deviceId);
        log.setLogMessage(logMessage);
        log.setExecutorId("대리점:" + agency.getLoginId());
        logRepository.save(log);

        return ResponseEntity.ok(savedUser);
    }
}