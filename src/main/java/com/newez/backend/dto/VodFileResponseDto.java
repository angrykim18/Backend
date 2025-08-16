package com.newez.backend.dto;

import com.newez.backend.domain.VodFile;
import lombok.Getter;

// ✅ [추가] 프론트엔드 응답을 위해 특별히 제작된 DTO (Data Transfer Object)
// 원본 Entity의 데이터를 오염시키지 않고, 프론트엔드에 필요한 데이터(fullUrl)를 추가로 담아 보냅니다.
@Getter
public class VodFileResponseDto {
    private Long id;
    private String vodFileNumber;
    private String vodFileName;
    private String vodFilePath; // 원본 상대 경로
    private Long vodContentId;
    private Integer fileOrder;
    private String fullUrl;     // 계산된 전체 URL

    public VodFileResponseDto(VodFile entity, String fullUrl) {
        this.id = entity.getId();
        this.vodFileNumber = entity.getVodFileNumber();
        this.vodFileName = entity.getVodFileName();
        this.vodFilePath = entity.getVodFilePath(); // DB에 저장된 원본 경로를 그대로 사용
        this.vodContentId = entity.getVodContentId();
        this.fileOrder = entity.getFileOrder();
        this.fullUrl = fullUrl; // 서버에서 계산한 전체 URL은 이 필드에만 저장
    }
}