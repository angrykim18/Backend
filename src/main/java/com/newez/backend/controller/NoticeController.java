package com.newez.backend.controller;

import com.newez.backend.domain.Notice;
import com.newez.backend.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class NoticeController {

    @Autowired
    private NoticeRepository noticeRepository;

    @GetMapping
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNoticeById(@PathVariable Long id) {
        return noticeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Notice createNotice(@RequestBody Notice notice) {
        return noticeRepository.save(notice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notice> updateNotice(@PathVariable Long id, @RequestBody Notice noticeDetails) {
        return noticeRepository.findById(id)
                .map(notice -> {
                    notice.setContent(noticeDetails.getContent());
                    notice.setTargetGroup(noticeDetails.getTargetGroup());
                    notice.setActive(noticeDetails.isActive());
                    Notice updatedNotice = noticeRepository.save(notice);
                    return ResponseEntity.ok(updatedNotice);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        return noticeRepository.findById(id)
                .map(notice -> {
                    noticeRepository.delete(notice);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}