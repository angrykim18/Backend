package com.newez.backend.controller;

import com.newez.backend.dto.WatchHistoryDto;
import com.newez.backend.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @PostMapping("/update")
    public ResponseEntity<Void> updateHistory(@RequestBody WatchHistoryDto historyDto) {
        historyService.saveOrUpdateHistory(historyDto);
        return ResponseEntity.ok().build();
    }
}