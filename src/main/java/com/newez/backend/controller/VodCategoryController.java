package com.newez.backend.controller;

import com.newez.backend.domain.VodCategory;
import com.newez.backend.domain.VodCategoryStructureDto; // ✅ [추가] DTO import
import com.newez.backend.repository.VodCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/vod-categories")
@CrossOrigin(origins = "*")
public class VodCategoryController {

    @Autowired
    private VodCategoryRepository vodCategoryRepository;

    @GetMapping
    public List<VodCategory> getAllCategories() {
        return vodCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VodCategory> getCategoryById(@PathVariable Long id) {
        return vodCategoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VodCategory createCategory(@RequestBody VodCategory vodCategory) {
        return vodCategoryRepository.save(vodCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VodCategory> updateCategory(@PathVariable Long id, @RequestBody VodCategory categoryDetails) {
        return vodCategoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setCategoryName(categoryDetails.getCategoryName());
                    existingCategory.setParentId(categoryDetails.getParentId());
                    existingCategory.setDisplayOrder(categoryDetails.getDisplayOrder());
                    VodCategory updatedCategory = vodCategoryRepository.save(existingCategory);
                    return ResponseEntity.ok(updatedCategory);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ [삭제] 기존 순서 업데이트 메소드는 더 강력한 새 API로 대체됩니다.
    /*
    @Transactional
    @PostMapping("/update-order")
    public ResponseEntity<Void> updateCategoryOrder(@RequestBody List<VodCategory> categories) {
        // ...
    }
    */

    // ✅ [추가] 드래그 앤 드롭으로 변경된 전체 구조(부모, 순서)를 저장하는 API
    @Transactional
    @PostMapping("/update-structure")
    public ResponseEntity<Void> updateCategoryStructure(@RequestBody List<VodCategoryStructureDto> categoryStructureList) {
        for (VodCategoryStructureDto dto : categoryStructureList) {
            vodCategoryRepository.findById(dto.getId()).ifPresent(category -> {
                category.setParentId(dto.getParentId());
                category.setDisplayOrder(dto.getDisplayOrder());
                vodCategoryRepository.save(category);
            });
        }
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        return vodCategoryRepository.findById(id)
                .map(category -> {
                    // 하위 카테고리가 있는지 확인하는 로직 추가 필요
                    vodCategoryRepository.delete(category);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}