package com.newez.backend.repository;

import com.newez.backend.domain.VodFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VodFileRepository extends JpaRepository<VodFile, Long> {

    // 특정 VOD 콘텐츠에 속한 모든 파일 목록을 찾는 기능
    List<VodFile> findByVodContentId(Long vodContentId);
}