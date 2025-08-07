package com.newez.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VodFileUpdateDto {

    private String vodFileName;   // 실제 파일 이름
    private String vodFilePath;
}