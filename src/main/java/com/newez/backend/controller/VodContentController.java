package com.newez.backend.controller;

import com.newez.backend.domain.VodContent;
import com.newez.backend.repository.VodContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
    public Page<VodContent> getContentsByCategoryId(
            @RequestParam Long categoryId,
            @RequestParam(required = false) String title,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        if (StringUtils.hasText(title)) {
            return vodContentRepository.findByCategoryIdAndTitleContaining(categoryId, title, pageable);
        } else {
            return vodContentRepository.findByCategoryId(categoryId, pageable);
        }
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<VodContent> updateContent(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("exposed") boolean exposed,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "poster", required = false) MultipartFile posterFile) {

        return vodContentRepository.findById(id)
                .map(existingContent -> {
                    existingContent.setTitle(title);
                    existingContent.setDescription(description);
                    existingContent.setExposed(exposed);
                    existingContent.setCategoryId(categoryId);

                    if (posterFile != null && !posterFile.isEmpty()) {
                        if (existingContent.getPosterPath() != null && !existingContent.getPosterPath().isEmpty()) {
                            try {
                                Files.deleteIfExists(Paths.get(existingContent.getPosterPath()));
                            } catch (IOException e) {
                                System.err.println("기존 포스터 파일 삭제 실패: " + existingContent.getPosterPath());
                            }
                        }

                        try {
                            String fileName = UUID.randomUUID().toString() + "_" + posterFile.getOriginalFilename();
                            Path filePath = Paths.get(uploadDir, fileName);
                            Files.write(filePath, posterFile.getBytes());
                            existingContent.setPosterPath(filePath.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    VodContent updatedContent = vodContentRepository.save(existingContent);
                    return ResponseEntity.ok(updatedContent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

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