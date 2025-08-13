package com.newez.backend.controller;

import com.newez.backend.domain.VodCategory;
import com.newez.backend.domain.VodCategoryStructureDto;
import com.newez.backend.dto.CategoryDto;
import com.newez.backend.dto.CategoryNodeDto;
import com.newez.backend.dto.CategoryReorderRequest;
import com.newez.backend.repository.VodCategoryRepository;
import com.newez.backend.service.VodCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vod-categories")
@CrossOrigin(origins = "*")
public class VodCategoryController {

    @Autowired
    private VodCategoryRepository vodCategoryRepository;

    @Autowired
    private VodCategoryService vodCategoryService;

    // === 신규: 웹/앱 모드 트리 ===
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryNodeDto>> getCategoryTree(@RequestParam(defaultValue = "web") String mode) {
        return ResponseEntity.ok(vodCategoryService.getCategoryTree(mode));
    }

    // === 신규: 순서 저장 ===
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderCategories(@RequestBody CategoryReorderRequest request) {
        vodCategoryService.reorderCategories(request);
        return ResponseEntity.ok().build();
    }

    // === 기존: 웹 전체 목록 ===
    @GetMapping("/all-for-web")
    public List<CategoryDto> getAllCategoriesForWeb() {
        return vodCategoryRepository.findAll().stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getCategoryName(), cat.getParentId()))
                .collect(Collectors.toList());
    }

    // === 기존: 안드로이드 (parentId 로 자식 조회) ===
    @GetMapping
    public List<CategoryDto> getCategoriesByParentId(@RequestParam(required = false) Long parentId) {
        List<VodCategory> categories;
        if (parentId == null) {
            categories = vodCategoryRepository.findByParentIdIsNull();
        } else {
            categories = vodCategoryRepository.findByParentId(parentId);
        }
        return categories.stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getCategoryName(), cat.getParentId()))
                .collect(Collectors.toList());
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
                    vodCategoryRepository.delete(category);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
