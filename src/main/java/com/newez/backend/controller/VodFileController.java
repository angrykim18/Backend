package com.newez.backend.controller;

import com.newez.backend.domain.VodFile;
import com.newez.backend.dto.VodFileResponseDto;
import com.newez.backend.dto.VodFileUpdateDto;
import com.newez.backend.service.VodfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vod-files")
@CrossOrigin(origins = "*")
public class VodFileController {

    @Autowired
    private VodfileService vodfileService;

    @GetMapping
    public Page<VodFileResponseDto> getFilesByContentId(
            @RequestParam Long contentId,
            Pageable pageable) {

        // ✅ [수정] 웹과 안드로이드 요청을 모두 처리하는 로직
        // 클라이언트(안드로이드)가 정렬을 요청했는지 확인합니다.
        if (pageable.getSort().isSorted()) {
            // 정렬 요청이 있으면(안드로이드), 그 요청을 그대로 사용합니다.
            return vodfileService.getFilesByContentId(contentId, pageable);
        } else {
            // 정렬 요청이 없으면(웹), 기본값(fileOrder 역순, 25개)을 설정합니다.
            Pageable webDefaultPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    25,
                    Sort.by("fileOrder").descending()
            );
            return vodfileService.getFilesByContentId(contentId, webDefaultPageable);
        }
    }

    @GetMapping("/all")
    public List<VodFileResponseDto> getAllFilesByContentId(@RequestParam Long contentId) {
        return vodfileService.getAllFilesByContentId(contentId);
    }

    @PostMapping
    public VodFile createVodFile(@RequestBody VodFile vodFile) {
        return vodfileService.saveFileInfo(vodFile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VodFile> updateVodFile(@PathVariable Long id, @RequestBody VodFileUpdateDto fileDetails) {
        try {
            VodFile updatedFile = vodfileService.updateVodFile(id, fileDetails);
            return ResponseEntity.ok(updatedFile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<Void> moveVodFile(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        try {
            Long newContentId = payload.get("newContentId");
            vodfileService.moveVodFile(id, newContentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/reorder")
    public ResponseEntity<String> reorderFiles(@RequestBody Map<String, Object> payload) {
        try {
            Long contentId = Long.valueOf(payload.get("contentId").toString());
            List<Map<String, Object>> files = (List<Map<String, Object>>) payload.get("files");

            vodfileService.reorderFiles(contentId, files);
            return ResponseEntity.ok("순서가 성공적으로 저장되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("순서 저장 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVodFile(@PathVariable Long id) {
        try {
            vodfileService.deleteVodFile(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}