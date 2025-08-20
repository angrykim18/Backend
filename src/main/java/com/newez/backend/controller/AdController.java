package com.newez.backend.controller;

import com.newez.backend.domain.Advertisement;
import com.newez.backend.repository.AdvertisementRepository;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/ads")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AdController {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 모든 광고 목록 조회
    @GetMapping
    public List<Advertisement> getAllAds() {
        return advertisementRepository.findAll();
    }

    // ✅ [기능 추가] 특정 광고 1개의 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<Advertisement> getAdById(@PathVariable Long id) {
        return advertisementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 새로운 광고 추가
    @PostMapping
    public Advertisement createAd(@RequestParam("adName") String adName,
                                  @RequestParam("targetGroup") String targetGroup,
                                  @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();
        String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path filePath = Paths.get(uploadDir + savedFileName);
        Files.write(filePath, imageFile.getBytes());

        Advertisement ad = new Advertisement();
        ad.setAdName(adName);
        ad.setTargetGroup(targetGroup);
        ad.setImagePath(filePath.toString());
        ad.setActive(true);

        return advertisementRepository.save(ad);
    }

    // ✅ [기능 추가] 특정 광고 정보 수정 (이미지 변경은 제외)
    @PutMapping("/{id}")
    public ResponseEntity<Advertisement> updateAd(@PathVariable Long id, @RequestBody Advertisement adDetails) {
        return advertisementRepository.findById(id)
                .map(ad -> {
                    ad.setAdName(adDetails.getAdName());
                    ad.setTargetGroup(adDetails.getTargetGroup());
                    ad.setActive(adDetails.isActive());
                    Advertisement updatedAd = advertisementRepository.save(ad);
                    return ResponseEntity.ok(updatedAd);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // 광고 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        return advertisementRepository.findById(id)
                .map(ad -> {
                    try {
                        Files.deleteIfExists(Paths.get(ad.getImagePath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    advertisementRepository.delete(ad);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}