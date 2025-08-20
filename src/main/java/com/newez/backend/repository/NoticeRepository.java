package com.newez.backend.repository;

import com.newez.backend.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findTopByNoticeTypeAndIsActiveAndTargetGroupOrderByCreatedAtDesc(String noticeType, boolean isActive, String targetGroup);
}