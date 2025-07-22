package com.newez.backend.controller;

import com.newez.backend.domain.VodFile;
import com.newez.backend.repository.VodFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vod-files")
@CrossOrigin(origins = "*")
public class VodFileController {

    @Autowired
    private VodFileRepository vodFileRepository;

    // 특정 VOD 콘텐츠에 속한 모든 파일 목록 조회
    // 예: /api/vod-files?contentId=123
    @GetMapping
    public List<VodFile> getFilesByContentId(@RequestParam Long contentId) {
        return vodFileRepository.findByVodContentId(contentId);
    }

    // 새로운 VOD 파일 정보 추가 (실제 파일 업로드는 별도 처리 필요)
    @PostMapping
    public VodFile createVodFile(@RequestBody VodFile vodFile) {
        return vodFileRepository.save(vodFile);
    }

    // VOD 파일 정보 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVodFile(@PathVariable Long id) {
        return vodFileRepository.findById(id)
                .map(vodFile -> {
                    vodFileRepository.delete(vodFile);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}