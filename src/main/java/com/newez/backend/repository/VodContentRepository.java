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

    // ✅ [수정] 트랜잭션 충돌을 일으키는 원인이었던 @Transactional 어노테이션을 삭제합니다.
    @Modifying
    @Query("UPDATE VodContent vc SET vc.updatedAt = CURRENT_TIMESTAMP WHERE vc.id = :id")
    void updateTimestamp(@Param("id") Long id);
}