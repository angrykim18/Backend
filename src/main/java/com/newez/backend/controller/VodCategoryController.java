package com.newez.backend.controller;

import com.newez.backend.domain.VodCategory;
import com.newez.backend.domain.VodCategoryStructureDto;
import com.newez.backend.dto.CategoryDto; // ✅ [추가] CategoryDto import
import com.newez.backend.repository.VodCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors; // ✅ [추가] stream import

@RestController
@RequestMapping("/api/vod-categories")
@CrossOrigin(origins = "*")
public class VodCategoryController {

    @Autowired
    private VodCategoryRepository vodCategoryRepository;

    // ✅ [수정] parentId를 기준으로 하위 카테고리를 DTO 형태로 반환하는 새로운 API
    @GetMapping
    public List<CategoryDto> getCategoriesByParentId(@RequestParam(required = false) Long parentId) {
        List<VodCategory> categories;
        if (parentId == null) {
            // parentId가 없으면 최상위 카테고리 (parentId가 null인 것들)를 반환
            categories = vodCategoryRepository.findByParentIdIsNull();
        } else {
            // parentId가 있으면 해당 하위 카테고리를 반환
            categories = vodCategoryRepository.findByParentId(parentId);
        }
        // 앱에서 사용하기 편한 CategoryDto 형태로 변환하여 반환
        return categories.stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getCategoryName()))
                .collect(Collectors.toList());
    }

    // --- 이하 다른 메소드들은 기존과 동일하게 유지됩니다 ---

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