package com.newez.backend.dto;

import java.util.List;

public class CategoryReorderRequest {

    public static class Item {
        private Long id;
        private Integer displayOrder;

        public Item() {}
        public Item(Long id, Integer displayOrder) {
            this.id = id; this.displayOrder = displayOrder;
        }
        public Long getId() { return id; }
        public Integer getDisplayOrder() { return displayOrder; }
        public void setId(Long id) { this.id = id; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    }


    private Long parentId;


    private List<Item> items;

    public CategoryReorderRequest() {}
    public CategoryReorderRequest(Long parentId, List<Item> items) {
        this.parentId = parentId; this.items = items;
    }

    public Long getParentId() { return parentId; }
    public List<Item> getItems() { return items; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public void setItems(List<Item> items) { this.items = items; }
}
