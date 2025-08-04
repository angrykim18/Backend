package com.newez.backend.service;

import com.newez.backend.domain.VodCategory;
import com.newez.backend.dto.CategoryDto;
import com.newez.backend.repository.VodCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VodCategoryService {

    private final VodCategoryRepository vodCategoryRepository;

    @Autowired
    public VodCategoryService(VodCategoryRepository vodCategoryRepository) {
        this.vodCategoryRepository = vodCategoryRepository;
    }

    // ✅ [수정] '영화MOVIE'의 하위 카테고리만 찾는 메소드
    public List<CategoryDto> findMovieSubCategories() {
        // 1. ✅ [수정] '영화MOVIE'의 id인 19를 직접 사용합니다.
        Long parentId = 19L;

        // 2. 부모의 ID를 가지고 하위 카테고리 목록을 DB에서 찾습니다.
        List<VodCategory> subCategories = vodCategoryRepository.findByParentId(parentId);

        // 3. 찾은 하위 카테고리 목록을 CategoryDto 목록으로 변환하여 반환합니다.
        return subCategories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getCategoryName()))
                .collect(Collectors.toList());
    }
}