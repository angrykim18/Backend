package com.newez.backend.repository;

import com.newez.backend.domain.User;
import com.newez.backend.domain.VodFile;
import com.newez.backend.domain.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {


    Optional<WatchHistory> findByUserAndVodFile(User user, VodFile vodFile);


    @Query("SELECT wh FROM WatchHistory wh WHERE wh.user = :user AND wh.vodFile.vodContentId = :contentId ORDER BY wh.updatedAt DESC LIMIT 1")
    Optional<WatchHistory> findTopByUserAndVodContentId(@Param("user") User user, @Param("contentId") Long contentId);

}