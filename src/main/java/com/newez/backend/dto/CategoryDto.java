package com.newez.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Getter, Setter, toString, EqualsAndHashCode 등을 자동으로 생성해 줍니다.
@Data
// 파라미터가 없는 기본 생성자를 생성합니다.
@NoArgsConstructor
// 모든 필드를 파라미터로 받는 생성자를 생성합니다.
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private Long parentId;
}