package com.newez.backend.repository;

import com.newez.backend.domain.LiveChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LiveChannelRepository extends JpaRepository<LiveChannel, Long> {

    // ✅ [안드로이드용] 전체 목록을 반환하는 메소드 (기존 유지)
    List<LiveChannel> findAllByOrderByDisplayOrderAsc();

    // ✅ [웹용] 페이지네이션 목록을 반환하는 메소드 (추가)
    Page<LiveChannel> findAllByOrderByDisplayOrderAsc(Pageable pageable);
}