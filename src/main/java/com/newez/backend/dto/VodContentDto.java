// com/newez/backend/dto/VodContentDto.java

package com.newez.backend.dto;

import com.newez.backend.domain.VodContent;
import com.newez.backend.domain.WatchHistory;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class VodContentDto {

    private Long id;
    private String title;
    private String description;
    private boolean exposed;
    private String posterPath;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long lastWatchedEpisodeId;
    private Integer lastWatchedTimestamp;
    private String lastWatchedEpisodeNumber; // ✅ [추가] 회차 이름을 담을 필드

    public VodContentDto(VodContent entity, String fullPosterUrl, Optional<WatchHistory> watchHistoryOpt) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.exposed = entity.isExposed();
        this.posterPath = fullPosterUrl; // ✅ [수정] 이 코드가 빠져있었습니다.
        this.categoryId = entity.getCategoryId();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();

        watchHistoryOpt.ifPresent(history -> {
            this.lastWatchedEpisodeId = history.getVodFile().getId();
            this.lastWatchedTimestamp = history.getTimestampSeconds();
            this.lastWatchedEpisodeNumber = history.getVodFile().getVodFileName(); // ✅ [추가] 회차 이름도 함께 저장
        });
    }
}