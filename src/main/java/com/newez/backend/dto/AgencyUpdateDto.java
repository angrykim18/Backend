package com.newez.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
public class AgencyUpdateDto {
    private String agencyName;
    private String password;
    private Integer dateCredits;
    private List<Integer> groupIds;
}