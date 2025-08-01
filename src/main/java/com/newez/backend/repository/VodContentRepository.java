package com.newez.backend.repository;

import com.newez.backend.domain.VodContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;      // ✅ [추가]
import org.springframework.data.jpa.repository.Query;          // ✅ [추가]
import org.springframework.data.repository.query.Param;      // ✅ [추가]
import org.springframework.transaction.annotation.Transactional; // ✅ [추가]

public interface VodContentRepository extends JpaRepository<VodContent, Long> {

    Page<VodContent> findByCategoryId(Long categoryId, Pageable pageable);

    Page<VodContent> findByCategoryIdAndTitleContaining(Long categoryId, String title, Pageable pageable);

    // ✅ [추가] ID를 기반으로 updatedAt 필드를 현재 시간으로 강제 갱신하는 쿼리
    @Transactional
    @Modifying
    @Query("UPDATE VodContent vc SET vc.updatedAt = CURRENT_TIMESTAMP WHERE vc.id = :id")
    void updateTimestamp(@Param("id") Long id);
}