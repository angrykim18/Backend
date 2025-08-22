package com.newez.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryNodeDto {
    private Long id;
    private String name;
    private String categoryName;
    private Long parentId;
    private Integer displayOrder;
    private List<CategoryNodeDto> children = new ArrayList<>();

    public CategoryNodeDto() {}

    public CategoryNodeDto(Long id, String name, Long parentId, Integer displayOrder) {
        this.id = id;
        this.name = name;
        this.categoryName = name;
        this.parentId = parentId;
        this.displayOrder = displayOrder;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategoryName() { return categoryName; }
    public Long getParentId() { return parentId; }
    public Integer getDisplayOrder() { return displayOrder; }
    public List<CategoryNodeDto> getChildren() { return children; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; this.categoryName = name; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public void setChildren(List<CategoryNodeDto> children) { this.children = children; }
}
