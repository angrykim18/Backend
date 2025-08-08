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

    public List<CategoryDto> findMovieSubCategories() {
        Long parentId = 19L;
        List<VodCategory> subCategories = vodCategoryRepository.findByParentId(parentId);

        // ✅ [수정] new CategoryDto() 생성자에 parentId 값을 추가하여 컴파일 에러를 해결합니다.
        return subCategories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getCategoryName(), category.getParentId()))
                .collect(Collectors.toList());
    }
}