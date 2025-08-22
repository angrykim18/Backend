package com.newez.backend.repository;

import com.newez.backend.domain.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    @Query("SELECT a FROM AppVersion a ORDER BY a.versionCode DESC LIMIT 1")
    Optional<AppVersion> findLatestVersion();
}