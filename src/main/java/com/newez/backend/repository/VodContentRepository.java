package com.newez.backend.repository;

import com.newez.backend.domain.VodContent;
import org.springframework.data.domain.Page;          // ✅ [추가] Page import
import org.springframework.data.domain.Pageable;        // ✅ [추가] Pageable import
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VodContentRepository extends JpaRepository<VodContent, Long> {

    // [삭제] 기존 List 반환 메소드는 더 이상 사용하지 않음
    // List<VodContent> findByCategoryId(Long categoryId);

    // ✅ [추가] 특정 카테고리의 콘텐츠를 페이지별로 조회하는 기능
    Page<VodContent> findByCategoryId(Long categoryId, Pageable pageable);

    // ✅ [추가] 특정 카테고리에서 제목으로 콘텐츠를 검색하고, 페이지별로 조회하는 기능
    Page<VodContent> findByCategoryIdAndTitleContaining(Long categoryId, String title, Pageable pageable);
}