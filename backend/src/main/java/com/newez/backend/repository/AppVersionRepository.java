package com.newez.backend.repository;

import com.newez.backend.domain.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {
    // 가장 최신 버전의 정보를 가져오는 기능을 추가합니다.
    @Query("SELECT a FROM AppVersion a ORDER BY a.versionCode DESC LIMIT 1")
    Optional<AppVersion> findLatestVersion();
}