package com.newez.backend.controller;

import com.newez.backend.domain.VodFile;
import com.newez.backend.repository.VodFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vod-files")
@CrossOrigin(origins = "*")
public class VodFileController {

    @Autowired
    private VodFileRepository vodFileRepository;

    // ✅ 정렬 기능이 적용된 Repository 메소드 호출로 수정
    @GetMapping
    public List<VodFile> getFilesByContentId(@RequestParam Long contentId) {
        return vodFileRepository.findByVodContentIdOrderByFileOrderAscUpdatedAtDesc(contentId);
    }

    // ✅ 실제 파일 업로드를 처리하는 API 추가
    @PostMapping("/upload")
    public VodFile uploadVodFile(
            @RequestParam("vodContentId") Long vodContentId,
            @RequestParam("vodFileNumber") String vodFileNumber,
            @RequestParam("vodFileName") String vodFileName,
            @RequestParam("file") MultipartFile file) {
        // (실제 파일 저장 로직은 여기에 구현됩니다)
        // String savedFilePath = "저장된 실제 파일 경로";

        VodFile newVodFile = new VodFile();
        newVodFile.setVodContentId(vodContentId);
        newVodFile.setVodFileNumber(vodFileNumber);
        newVodFile.setVodFileName(vodFileName);
        // newVodFile.setVodFilePath(savedFilePath);

        return vodFileRepository.save(newVodFile);
    }

    // ✅ 파일 순서 변경을 처리하는 API 추가
    @Transactional
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderFiles(@RequestBody List<Map<String, Object>> payload) {
        for (Map<String, Object> item : payload) {
            Long id = Long.valueOf(item.get("id").toString());
            Integer order = (Integer) item.get("order");
            vodFileRepository.findById(id).ifPresent(vodFile -> {
                vodFile.setFileOrder(order);
                vodFileRepository.save(vodFile);
            });
        }
        return ResponseEntity.ok().build();
    }

    // VOD 파일 정보 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVodFile(@PathVariable Long id) {
        // (실제 저장된 파일 삭제 로직 추가 필요)
        return vodFileRepository.findById(id)
                .map(vodFile -> {
                    vodFileRepository.delete(vodFile);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}