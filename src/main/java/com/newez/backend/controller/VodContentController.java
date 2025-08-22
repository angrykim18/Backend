package com.newez.backend.controller;

import com.newez.backend.domain.User;
import com.newez.backend.domain.VodContent;
import com.newez.backend.domain.WatchHistory;
import com.newez.backend.dto.VodContentDto;
import com.newez.backend.repository.UserRepository;
import com.newez.backend.repository.VodContentRepository;
import com.newez.backend.repository.WatchHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
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
import com.newez.backend.util.ChosungUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vod-contents")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class VodContentController {

    @Autowired
    private VodContentRepository vodContentRepository;

    // ✅ [추가] 시청 기록 조회를 위한 Repository 주입
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WatchHistoryRepository watchHistoryRepository;

    @Value("${poster.upload-dir}")
    private String uploadDir;

    private final String POSTER_URL_PATH = "posters";

    // ✅ [추가] 새로운 전체 검색 API
    @GetMapping("/search")
    public Page<VodContentDto> searchContents(
            @RequestParam String title,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {

        Page<VodContent> contentsPage = vodContentRepository.findByTitleOrChosung(title, pageable);
        String baseUrl = getBaseUrl(request);

        return contentsPage.map(content -> {
            String fullPosterUrl = buildFullPosterUrl(baseUrl, content.getPosterPath());
            return new VodContentDto(content, fullPosterUrl, Optional.empty());
        });
    }

    // ✅ [추가] 기존 데이터를 위해 초성 데이터를 채워주는 일회성 마이그레이션 API
    // 이 API는 DB에 데이터가 쌓인 후 딱 한 번만 실행하면 됩니다.
    @GetMapping("/migrate-chosung")
    @Transactional
    public ResponseEntity<String> migrateChosungData() {
        List<VodContent> allContents = vodContentRepository.findAll();
        for (VodContent content : allContents) {
            if (content.getTitle() != null && (content.getTitleChosung() == null || content.getTitleChosung().isEmpty())) {
                String chosung = ChosungUtils.extractChosung(content.getTitle());
                content.setTitleChosung(chosung);
            }
        }
        vodContentRepository.saveAll(allContents);
        return ResponseEntity.ok("데이터 마이그레이션 성공: " + allContents.size() + "개 항목 처리 완료");
    }

    @GetMapping
    public Page<VodContentDto> getContentsByCategoryId(
            @RequestParam Long categoryId,
            @RequestParam(required = false) String title,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {

        Page<VodContent> contentsPage;
        if (StringUtils.hasText(title)) {
            contentsPage = vodContentRepository.findByCategoryIdAndTitleContaining(categoryId, title, pageable);
        } else {
            contentsPage = vodContentRepository.findByCategoryId(categoryId, pageable);
        }

        //String baseUrl = getBaseUrl(request);

        return contentsPage.map(content -> {
            //String fullPosterUrl = buildFullPosterUrl(baseUrl, content.getPosterPath());
            String relativePosterPath = "/" + content.getPosterPath();
            return new VodContentDto(content, relativePosterPath, Optional.empty());
        });
    }

    @GetMapping("/{id}")
    public ResponseEntity<VodContentDto> getContentById(
            @PathVariable Long id,
            @RequestParam(required = false) String deviceId, // ✅ [추가] deviceId 파라미터
            HttpServletRequest request) {

        String baseUrl = getBaseUrl(request);

        return vodContentRepository.findById(id)
                .map(content -> {
                    // ✅ [추가] deviceId로 사용자를 찾고, 시청 기록을 조회하는 로직
                    Optional<User> userOpt = (deviceId != null && !deviceId.isEmpty()) ? userRepository.findByDeviceId(deviceId) : Optional.empty();
                    Optional<WatchHistory> historyOpt = userOpt.flatMap(
                            user -> watchHistoryRepository.findTopByUserAndVodContentId(user, content.getId())
                    );

                    String fullPosterUrl = buildFullPosterUrl(baseUrl, content.getPosterPath());
                    // ✅ [수정] 조회된 시청 기록과 함께 DTO를 생성
                    VodContentDto dto = new VodContentDto(content, fullPosterUrl, historyOpt);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VodContentDto> createContent(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("exposed") boolean exposed,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "poster", required = false) MultipartFile posterFile,
            HttpServletRequest request) {

        VodContent newContent = new VodContent();
        newContent.setTitle(title);
        newContent.setDescription(description);
        newContent.setExposed(exposed);
        newContent.setCategoryId(categoryId);
        newContent.setTitleChosung(ChosungUtils.extractChosung(title));

        if (posterFile != null && !posterFile.isEmpty()) {
            try {
                String posterRelativePath = savePosterFile(posterFile);
                newContent.setPosterPath(posterRelativePath);
            } catch (IOException e) {
                return ResponseEntity.status(500).build();
            }
        }
        VodContent savedContent = vodContentRepository.save(newContent);
        String baseUrl = getBaseUrl(request);
        String fullPosterUrl = buildFullPosterUrl(baseUrl, savedContent.getPosterPath());
        // ✅ [수정] 시청 기록이 없는 DTO 생성자로 변경
        return ResponseEntity.ok(new VodContentDto(savedContent, fullPosterUrl, Optional.empty()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VodContentDto> updateContent(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("exposed") boolean exposed,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "poster", required = false) MultipartFile posterFile,
            HttpServletRequest request) {

        return vodContentRepository.findById(id)
                .map(existingContent -> {
                    existingContent.setTitle(title);
                    existingContent.setDescription(description);
                    existingContent.setExposed(exposed);
                    existingContent.setCategoryId(categoryId);
                    existingContent.setTitleChosung(ChosungUtils.extractChosung(title));

                    if (posterFile != null && !posterFile.isEmpty()) {
                        deletePosterFile(existingContent.getPosterPath());
                        try {
                            String posterRelativePath = savePosterFile(posterFile);
                            existingContent.setPosterPath(posterRelativePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    VodContent updatedContent = vodContentRepository.save(existingContent);
                    String baseUrl = getBaseUrl(request);
                    String fullPosterUrl = buildFullPosterUrl(baseUrl, updatedContent.getPosterPath());
                    // ✅ [수정] 시청 기록이 없는 DTO 생성자로 변경
                    return ResponseEntity.ok(new VodContentDto(updatedContent, fullPosterUrl, Optional.empty()));
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
                deletePosterFile(content.getPosterPath());
            }
            vodContentRepository.deleteAllById(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // --- Helper Methods ---
    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    private String buildFullPosterUrl(String baseUrl, String posterPath) {
        if (posterPath != null && !posterPath.isEmpty()) {
            return baseUrl + "/" + posterPath;
        }
        return "";
    }

    private String savePosterFile(MultipartFile posterFile) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        String fileName = UUID.randomUUID().toString() + "_" + posterFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, posterFile.getBytes());
        return POSTER_URL_PATH + "/" + fileName;
    }

    private void deletePosterFile(String posterPath) {
        if (posterPath != null && !posterPath.isEmpty()) {
            try {
                Path fileToDeletePath = Paths.get(uploadDir, posterPath.replace(POSTER_URL_PATH + "/", ""));
                Files.deleteIfExists(fileToDeletePath);
            } catch (IOException e) {
                System.err.println("기존 포스터 파일 삭제 실패: " + posterPath);
            }
        }
    }
}