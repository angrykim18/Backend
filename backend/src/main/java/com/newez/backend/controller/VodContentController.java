package com.newez.backend.controller;

import com.newez.backend.domain.VodContent;
import com.newez.backend.repository.VodContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map; // ✅ Map import 추가
import java.util.UUID;

@RestController
@RequestMapping("/api/vod-contents")
@CrossOrigin(origins = "*")
public class VodContentController {

    @Autowired
    private VodContentRepository vodContentRepository;

    @Value("${poster.upload-dir}")
    private String uploadDir;

    @GetMapping
    public List<VodContent> getContentsByCategoryId(@RequestParam Long categoryId) {
        return vodContentRepository.findByCategoryId(categoryId);
    }

    // ✅ [기능 추가] ID로 특정 콘텐츠 하나의 정보를 조회하는 API
    @GetMapping("/{id}")
    public ResponseEntity<VodContent> getContentById(@PathVariable Long id) {
        return vodContentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VodContent> createContent(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("exposed") boolean exposed,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "poster", required = false) MultipartFile posterFile) {

        VodContent newContent = new VodContent();
        newContent.setTitle(title);
        newContent.setDescription(description);
        newContent.setExposed(exposed);
        newContent.setCategoryId(categoryId);

        if (posterFile != null && !posterFile.isEmpty()) {
            try {
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = UUID.randomUUID().toString() + "_" + posterFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.write(filePath, posterFile.getBytes());
                newContent.setPosterPath(filePath.toString());
            } catch (IOException e) {
                return ResponseEntity.status(500).build();
            }
        }
        VodContent savedContent = vodContentRepository.save(newContent);
        return ResponseEntity.ok(savedContent);
    }

    // ✅ [기능 추가] VOD 콘텐츠 정보를 수정하는 API
    @PutMapping("/{id}")
    public ResponseEntity<VodContent> updateContent(@PathVariable Long id, @RequestBody VodContent contentDetails) {
        return vodContentRepository.findById(id)
                .map(existingContent -> {
                    existingContent.setTitle(contentDetails.getTitle());
                    existingContent.setDescription(contentDetails.getDescription());
                    existingContent.setExposed(contentDetails.isExposed());
                    existingContent.setCategoryId(contentDetails.getCategoryId());
                    // 포스터 경로는 파일 업로드가 없으므로 직접 수정하지 않거나, 별도 API로 처리
                    VodContent updatedContent = vodContentRepository.save(existingContent);
                    return ResponseEntity.ok(updatedContent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ [기능 추가] 선택된 콘텐츠들을 다른 카테고리로 이동시키는 API
    @Transactional
    @PatchMapping("/move")
    public ResponseEntity<Void> moveContents(@RequestBody Map<String, Object> payload) {
        List<Integer> idsAsInteger = (List<Integer>) payload.get("ids");
        List<Long> ids = idsAsInteger.stream().map(Long::valueOf).toList();
        Long categoryId = Long.valueOf(payload.get("categoryId").toString());

        List<VodContent> contentsToMove = vodContentRepository.findAllById(ids);
        for (VodContent content : contentsToMove) {
            content.setCategoryId(categoryId);
        }
        vodContentRepository.saveAll(contentsToMove);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<Void> deleteContents(@RequestBody List<Long> ids) {
        try {
            List<VodContent> contentsToDelete = vodContentRepository.findAllById(ids);
            for (VodContent content : contentsToDelete) {
                if (content.getPosterPath() != null && !content.getPosterPath().isEmpty()) {
                    try {
                        Files.deleteIfExists(Paths.get(content.getPosterPath()));
                    } catch (IOException e) {
                        System.err.println("Failed to delete poster file: " + content.getPosterPath());
                    }
                }
            }
            vodContentRepository.deleteAllById(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}