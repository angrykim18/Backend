package com.newez.backend.repository;

import com.newez.backend.domain.VodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional; // ✅ [추가] Optional import

public interface VodCategoryRepository extends JpaRepository<VodCategory, Long> {

    List<VodCategory> findAllByOrderByDisplayOrderAsc();

    List<VodCategory> findByParentId(Long parentId);

    List<VodCategory> findByParentIdIsNull();

    // ✅ [추가] 카테고리 이름으로 카테고리를 찾는 기능을 추가합니다.
    // 결과가 없을 수도 있으므로 Optional로 감싸서 반환합니다.
    Optional<VodCategory> findByCategoryName(String categoryName);
}