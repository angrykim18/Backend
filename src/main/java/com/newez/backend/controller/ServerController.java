package com.newez.backend.controller;

import com.newez.backend.domain.Server;
import com.newez.backend.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servers")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class ServerController {

    @Autowired
    private ServerRepository serverRepository;

    // 모든 서버 목록 조회
    @GetMapping
    public List<Server> getAllServers() {
        return serverRepository.findAll();
    }

    // ✅ [기능 추가] 특정 서버 1개의 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<Server> getServerById(@PathVariable Long id) {
        return serverRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 새로운 서버 추가
    @PostMapping
    public Server createServer(@RequestBody Server server) {
        return serverRepository.save(server);
    }

    // 특정 서버 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Server> updateServer(@PathVariable Long id, @RequestBody Server serverDetails) {
        return serverRepository.findById(id)
                .map(server -> {
                    server.setServerName(serverDetails.getServerName());
                    server.setServerUrl(serverDetails.getServerUrl());
                    server.setDescription(serverDetails.getDescription());
                    Server updatedServer = serverRepository.save(server);
                    return ResponseEntity.ok(updatedServer);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 특정 서버 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServer(@PathVariable Long id) {
        serverRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}