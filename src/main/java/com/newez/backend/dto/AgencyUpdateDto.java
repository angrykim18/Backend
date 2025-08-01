package com.newez.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

// ✅ [추가] 대리점 '수정' 시에만 사용할 DTO
@Getter
@Setter
public class AgencyUpdateDto {
    private String agencyName;
    private String password;
    private Integer dateCredits;
    private List<Integer> groupIds;
}