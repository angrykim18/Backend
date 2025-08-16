package com.newez.backend.controller;

import com.newez.backend.domain.AppVersion;
import com.newez.backend.repository.AppVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/updates")
@CrossOrigin(origins = "http://localhost:3000")
public class AppUpdateController {

    @Autowired
    private AppVersionRepository appVersionRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    // 안드로이드 앱이 최신 버전을 확인하는 API
    @GetMapping("/latest")
    public ResponseEntity<AppVersion> getLatestVersion() {
        Optional<AppVersion> latestVersion = appVersionRepository.findLatestVersion();
        return latestVersion.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 관리자 페이지에서 모든 버전 목록을 보는 API
    @GetMapping
    public List<AppVersion> getAllVersions() {
        return appVersionRepository.findAll();
    }

    // 관리자 페이지에서 새로운 앱 버전을 업로드하는 API
    @PostMapping
    public AppVersion uploadNewVersion(
            @RequestParam("versionCode") int versionCode,
            @RequestParam("versionName") String versionName,
            @RequestParam("releaseNotes") String releaseNotes,
            @RequestParam("appFile") MultipartFile appFile) throws IOException {

        String fileName = appFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent()); // 폴더가 없으면 생성
        Files.write(filePath, appFile.getBytes());

        AppVersion newVersion = new AppVersion();
        newVersion.setVersionCode(versionCode);
        newVersion.setVersionName(versionName);
        newVersion.setReleaseNotes(releaseNotes);
        newVersion.setFilePath(filePath.toString());

        return appVersionRepository.save(newVersion);
    }

    // ✅ [기능 추가] 특정 버전 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVersion(@PathVariable Long id) {
        return appVersionRepository.findById(id)
                .map(appVersion -> {
                    // DB에서 정보를 삭제하기 전에, 실제 저장된 .apk 파일도 삭제합니다.
                    try {
                        Files.deleteIfExists(Paths.get(appVersion.getFilePath()));
                    } catch (IOException e) {
                        e.printStackTrace(); // 파일 삭제 실패 시 로그만 남깁니다.
                    }
                    appVersionRepository.delete(appVersion);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}