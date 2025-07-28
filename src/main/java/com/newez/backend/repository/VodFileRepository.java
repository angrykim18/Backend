package com.newez.backend.repository;

import com.newez.backend.domain.VodFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VodFileRepository extends JpaRepository<VodFile, Long> {

    // ✅ [수정] 정렬 기능 추가
    List<VodFile> findByVodContentIdOrderByFileOrderAscUpdatedAtDesc(Long vodContentId);
}