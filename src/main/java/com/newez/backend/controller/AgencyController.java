package com.newez.backend.controller;

import com.newez.backend.domain.Agency;
import com.newez.backend.domain.Log;
import com.newez.backend.dto.AgencyUpdateDto;
import com.newez.backend.repository.AgencyRepository;
import com.newez.backend.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/agencies")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AgencyController {

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agency> getAgencyById(@PathVariable Long id) {
        return agencyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/groups")
    public List<Integer> getManagedGroupIds(@PathVariable Long id) {
        String sql = "SELECT group_id FROM agency_group_mappings WHERE agency_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, id);
    }

    @PostMapping
    public Agency createAgency(@RequestBody Agency agency) {
        return agencyRepository.save(agency);
    }

    // ✅ [수정] Map 대신 안전한 AgencyUpdateDto를 사용하도록 변경
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Agency> updateAgency(@PathVariable Long id, @RequestBody AgencyUpdateDto agencyDetails) {
        return agencyRepository.findById(id)
                .map(agency -> {
                    agency.setAgencyName(agencyDetails.getAgencyName());
                    String password = agencyDetails.getPassword();
                    if (password != null && !password.isEmpty()) {
                        agency.setPassword(password);
                    }
                    agency.setDateCredits(agencyDetails.getDateCredits());

                    agencyRepository.save(agency);

                    List<Integer> groupIds = agencyDetails.getGroupIds();
                    if (groupIds != null) {
                        jdbcTemplate.update("DELETE FROM agency_group_mappings WHERE agency_id = ?", id);
                        for (Integer groupId : groupIds) {
                            jdbcTemplate.update("INSERT INTO agency_group_mappings (agency_id, group_id) VALUES (?, ?)", id, groupId);
                        }
                    }
                    return ResponseEntity.ok(agency);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgency(@PathVariable Long id) {
        jdbcTemplate.update("DELETE FROM agency_group_mappings WHERE agency_id = ?", id);
        agencyRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ✅ [복원] 생략되었던 메소드 내용 전체 복원
    @PostMapping("/{id}/charge")
    public ResponseEntity<Agency> chargeCredits(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer amount = payload.get("amount");
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return agencyRepository.findById(id)
                .map(agency -> {
                    agency.setDateCredits(agency.getDateCredits() + amount);
                    agencyRepository.save(agency);

                    Log log = new Log();
                    log.setLogType("크레딧 충전");
                    String logMessage = String.format("총관리자가 '%s' 대리점에 %d일 크레딧을 충전했습니다.", agency.getAgencyName(), amount);
                    log.setLogMessage(logMessage);
                    log.setExecutorId("최고관리자");
                    logRepository.save(log);

                    return ResponseEntity.ok(agency);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ [복원] 생략되었던 메소드 내용 전체 복원
    @PostMapping("/login")
    public ResponseEntity<?> agencyLogin(@RequestBody Map<String, String> loginRequest) {
        String loginId = loginRequest.get("loginId");
        String password = loginRequest.get("password");

        Optional<Agency> agencyOptional = agencyRepository.findByLoginId(loginId);

        if (agencyOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        Agency agency = agencyOptional.get();

        if (password.equals(agency.getPassword())) {
            return ResponseEntity.ok(agency);
        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }
}