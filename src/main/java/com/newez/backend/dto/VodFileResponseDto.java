package com.newez.backend.dto;

import com.newez.backend.domain.VodFile;
import lombok.Getter;


@Getter
public class VodFileResponseDto {
    private Long id;
    private String vodFileNumber;
    private String vodFileName;
    private String vodFilePath;
    private Long vodContentId;
    private Integer fileOrder;
    private String fullUrl;

    public VodFileResponseDto(VodFile entity, String fullUrl) {
        this.id = entity.getId();
        this.vodFileNumber = entity.getVodFileNumber();
        this.vodFileName = entity.getVodFileName();
        this.vodFilePath = entity.getVodFilePath();
        this.vodContentId = entity.getVodContentId();
        this.fileOrder = entity.getFileOrder();
        this.fullUrl = fullUrl;
    }
}