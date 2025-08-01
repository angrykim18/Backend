package com.newez.backend.dto;

import com.newez.backend.domain.VodContent;
import lombok.Getter;
import java.time.LocalDateTime;

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

    public VodContentDto(VodContent entity, String fullPosterUrl) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.exposed = entity.isExposed();
        this.posterPath = fullPosterUrl;
        this.categoryId = entity.getCategoryId();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}