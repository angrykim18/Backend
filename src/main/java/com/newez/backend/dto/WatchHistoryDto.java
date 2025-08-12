package com.newez.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WatchHistoryDto {
    private String deviceId;
    private Long vodFileId;
    private int timestampSeconds;
}