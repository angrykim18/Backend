package com.newez.backend.repository;

import com.newez.backend.domain.VodContent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VodContentRepository extends JpaRepository<VodContent, Long> {

    // 특정 카테고리에 속한 VOD 콘텐츠 목록을 찾는 기능
    List<VodContent> findByCategoryId(Long categoryId);
}