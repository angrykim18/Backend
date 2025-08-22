package com.newez.backend.repository;

import com.newez.backend.domain.VodContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional; // ✅ [수정] 이 줄은 삭제하거나, 아래 어노테이션을 지우면 IDE가 자동으로 삭제합니다.

public interface VodContentRepository extends JpaRepository<VodContent, Long> {

    Page<VodContent> findByCategoryId(Long categoryId, Pageable pageable);

    Page<VodContent> findByCategoryIdAndTitleContaining(Long categoryId, String title, Pageable pageable);


    @Modifying
    @Query("UPDATE VodContent vc SET vc.updatedAt = CURRENT_TIMESTAMP WHERE vc.id = :id")
    void updateTimestamp(@Param("id") Long id);

    @Query("SELECT vc FROM VodContent vc JOIN VodCategory cat ON vc.categoryId = cat.id " +
            "WHERE (vc.title LIKE %:keyword% OR vc.titleChosung LIKE %:keyword%) " +
            "AND cat.categoryName NOT LIKE '%성인%'")
    Page<VodContent> findByTitleOrChosung(@Param("keyword") String keyword, Pageable pageable);
}