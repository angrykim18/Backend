package com.newez.backend.controller;

import com.newez.backend.domain.Agency;
import com.newez.backend.domain.Log;
import com.newez.backend.domain.User;
import com.newez.backend.dto.ActivationResponse;
import com.newez.backend.dto.ComprehensiveUserResponse; // [추가]
import com.newez.backend.dto.IpUpdateRequestDto;
import com.newez.backend.repository.AgencyRepository;
import com.newez.backend.repository.LogRepository;
import com.newez.backend.repository.UserRepository;
import com.newez.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;
    private final LogRepository logRepository;
    private final UserService userService;

    // --- 다른 메소드들은 그대로 둡니다 ---
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

    /**
     * [수정] 이 메소드의 반환 타입을 User에서 ComprehensiveUserResponse로 변경합니다.
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<ComprehensiveUserResponse> getUserByDeviceId(@PathVariable String deviceId) {
        try {
            String decodedDeviceId = URLDecoder.decode(deviceId, StandardCharsets.UTF_8.toString());

            // 1. UserService의 새로운 메소드를 호출합니다.
            ComprehensiveUserResponse responseData = userService.getComprehensiveUserInfo(decodedDeviceId);

            // 2. '종합 선물 세트'를 성공(200 OK) 응답으로 보냅니다.
            return ResponseEntity.ok(responseData);

        } catch (IllegalArgumentException e) {
            // 사용자를 찾지 못한 경우 404 Not Found 응답
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // 그 외 에러는 400 Bad Request 응답
            System.out.println("[오류] getUserByDeviceId 처리 중 에러: " + e.getMessage());
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
        newUser.setSubscriptionEndDate(new Date(System.currentTimeMillis()).toLocalDate());
        User savedUser = userRepository.save(newUser);

        Log log = new Log();
        log.setLogType("신규 개통");
        String logMessage = String.format("'%s' 대리점이 새로운 기기(%s)를 개통했습니다.", agency.getAgencyName(), deviceId);
        log.setLogMessage(logMessage);
        log.setExecutorId("대리점:" + agency.getLoginId());
        logRepository.save(log);

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/device/{deviceId}/ip")
    public ResponseEntity<Void> updateUserIp(
            @PathVariable String deviceId,
            @RequestBody IpUpdateRequestDto requestDto) {

        userService.updateUserIpByDeviceId(deviceId, requestDto.getIp());
        return ResponseEntity.ok().build();
    }
}
