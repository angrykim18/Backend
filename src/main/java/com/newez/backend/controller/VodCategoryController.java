package com.newez.backend.controller;

import com.newez.backend.domain.VodCategory;
import com.newez.backend.domain.VodCategoryStructureDto;
import com.newez.backend.dto.CategoryDto;
import com.newez.backend.repository.VodCategoryRepository;
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

    // ✅ [추가] 웹 관리자 페이지의 VOD 관리 메뉴를 위한 새로운 API입니다.
    // 모든 카테고리 목록을 parentId 정보와 함께 반환하여, 프론트엔드가 트리 구조를 만들 수 있도록 합니다.
    @GetMapping("/all-for-web")
    public List<CategoryDto> getAllCategoriesForWeb() {
        return vodCategoryRepository.findAll().stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getCategoryName(), cat.getParentId()))
                .collect(Collectors.toList());
    }

    // ✅ [수정] 기존 API도 수정된 CategoryDto를 사용하도록 변경합니다. (안드로이드 앱은 이 API를 사용합니다)
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