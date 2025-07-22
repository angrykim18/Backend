package com.newez.backend.controller;

import com.newez.backend.domain.VodCategory;
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
        // ✅ [핵심 수정] displayOrder 순서대로 정렬된 목록을 반환하도록 변경합니다.
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
                    // ✅ [추가] 순서 정보도 함께 업데이트되도록 추가합니다.
                    existingCategory.setDisplayOrder(categoryDetails.getDisplayOrder());
                    VodCategory updatedCategory = vodCategoryRepository.save(existingCategory);
                    return ResponseEntity.ok(updatedCategory);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @PostMapping("/update-order")
    public ResponseEntity<Void> updateCategoryOrder(@RequestBody List<VodCategory> categories) {
        AtomicInteger orderCounter = new AtomicInteger(0);
        categories.forEach(categoryData -> {
            vodCategoryRepository.findById(categoryData.getId()).ifPresent(category -> {
                category.setDisplayOrder(orderCounter.getAndIncrement());
                vodCategoryRepository.save(category);
            });
        });
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        return vodCategoryRepository.findById(id)
                .map(category -> {
                    vodCategoryRepository.delete(category);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}