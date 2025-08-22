package com.newez.backend.controller;

import com.newez.backend.domain.Log;
import com.newez.backend.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class LogController {

    @Autowired
    private LogRepository logRepository;

    @GetMapping
    public List<Log> getAllLogs() {
        // 최신순으로 정렬하여 모든 로그를 반환합니다.
        return logRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
}