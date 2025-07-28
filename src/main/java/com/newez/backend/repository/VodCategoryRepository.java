package com.newez.backend.repository;

import com.newez.backend.domain.VodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VodCategoryRepository extends JpaRepository<VodCategory, Long> {

    List<VodCategory> findAllByOrderByDisplayOrderAsc();
    // 부모 ID로 하위 카테고리 목록을 찾는 기능
    List<VodCategory> findByParentId(Long parentId);

    // 최상위 카테고리 (부모가 없는) 목록을 찾는 기능
    List<VodCategory> findByParentIdIsNull();
}