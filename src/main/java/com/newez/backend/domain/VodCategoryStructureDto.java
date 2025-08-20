package com.newez.backend.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VodCategoryStructureDto {
    private Long id;
    private Long parentId;
    private Integer displayOrder;
}