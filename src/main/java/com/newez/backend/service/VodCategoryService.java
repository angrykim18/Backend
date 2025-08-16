package com.newez.backend.service;

import com.newez.backend.domain.VodCategory;
import com.newez.backend.dto.CategoryDto;
import com.newez.backend.dto.CategoryNodeDto;
import com.newez.backend.dto.CategoryReorderRequest;
import com.newez.backend.repository.VodCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VodCategoryService {

    private final VodCategoryRepository vodCategoryRepository;

    public VodCategoryService(VodCategoryRepository vodCategoryRepository) {
        this.vodCategoryRepository = vodCategoryRepository;
    }

    // === 기존 유지 ===
    public List<CategoryDto> findMovieSubCategories() {
        Long parentId = 19L;
        List<VodCategory> subCategories = vodCategoryRepository.findByParentId(parentId);
        return subCategories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getCategoryName(), category.getParentId()))
                .collect(Collectors.toList());
    }

    // === 신규: 웹/앱 트리 ===
    public List<CategoryNodeDto> getCategoryTree(String mode) {
        int depthLimit = "app".equalsIgnoreCase(mode) ? 3 : 2;

        List<VodCategory> all = vodCategoryRepository.findAllByOrderByDisplayOrderAsc();
        Set<Long> existingIds = all.stream().map(VodCategory::getId).collect(Collectors.toSet());

        Map<Long, List<VodCategory>> byParent = new HashMap<>();
        for (VodCategory c : all) {
            Long p = c.getParentId();
            if (p != null && !existingIds.contains(p)) p = null; // 고아 승격
            byParent.computeIfAbsent(p, k -> new ArrayList<>()).add(c);
        }

        byParent.values().forEach(list -> list.sort(
                Comparator.comparing(VodCategory::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(VodCategory::getId)));

        List<VodCategory> roots = byParent.getOrDefault(null, Collections.emptyList());
        List<CategoryNodeDto> rootNodes = roots.stream().map(this::toNode).collect(Collectors.toList());
        for (CategoryNodeDto r : rootNodes) buildChildren(r, byParent, depthLimit - 1);
        return rootNodes;
    }

    private void buildChildren(CategoryNodeDto parent,
                               Map<Long, List<VodCategory>> byParent,
                               int depthRemain) {
        if (depthRemain <= 0) return;
        List<VodCategory> children = byParent.getOrDefault(parent.getId(), Collections.emptyList());
        for (VodCategory c : children) {
            CategoryNodeDto child = toNode(c);
            parent.getChildren().add(child);
            buildChildren(child, byParent, depthRemain - 1);
        }
    }

    private CategoryNodeDto toNode(VodCategory c) {
        return new CategoryNodeDto(c.getId(), c.getCategoryName(), c.getParentId(), c.getDisplayOrder());
    }

    // === 신규: 순서 저장 ===
    @Transactional
    public void reorderCategories(CategoryReorderRequest req) {
        if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("No items to reorder");
        }

        Long parentId = req.getParentId();
        List<CategoryReorderRequest.Item> items = req.getItems();

        Set<Long> idSet = new HashSet<>();
        for (CategoryReorderRequest.Item it : items) {
            if (it.getId() == null) throw new IllegalArgumentException("Item id is null");
            if (!idSet.add(it.getId())) throw new IllegalArgumentException("Duplicate item id: " + it.getId());
            if (it.getDisplayOrder() == null || it.getDisplayOrder() <= 0)
                throw new IllegalArgumentException("displayOrder must be positive");
        }

        Map<Long, VodCategory> map = vodCategoryRepository.findAllById(idSet).stream()
                .collect(Collectors.toMap(VodCategory::getId, x -> x));

        for (Long id : idSet) {
            VodCategory c = map.get(id);
            if (c == null) throw new IllegalArgumentException("Category not found: " + id);
            if (!Objects.equals(c.getParentId(), parentId)) {
                throw new IllegalArgumentException("Parent mismatch for id " + id +
                        " (expected " + parentId + ", actual " + c.getParentId() + ")");
            }
        }

        int n = items.size();
        Set<Integer> orders = items.stream().map(CategoryReorderRequest.Item::getDisplayOrder).collect(Collectors.toSet());
        if (orders.size() != n) throw new IllegalArgumentException("displayOrder has duplicates");
        for (int i = 1; i <= n; i++) if (!orders.contains(i))
            throw new IllegalArgumentException("displayOrder must be 1.." + n + " contiguous");

        for (CategoryReorderRequest.Item it : items) {
            VodCategory c = map.get(it.getId());
            c.setDisplayOrder(it.getDisplayOrder());
        }
        vodCategoryRepository.saveAll(map.values());
    }

    // === 기존 CRUD 도우미 (컨트롤러가 사용) ===
    public List<VodCategory> findAll() {
        return vodCategoryRepository.findAll();
    }

    public VodCategory findById(Long id) {
        return vodCategoryRepository.findById(id).orElse(null);
    }

    public VodCategory save(VodCategory category) {
        return vodCategoryRepository.save(category);
    }

    public void deleteById(Long id) {
        vodCategoryRepository.deleteById(id);
    }
}
