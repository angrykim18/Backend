package com.newez.backend.repository;

import com.newez.backend.domain.VodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VodCategoryRepository extends JpaRepository<VodCategory, Long> {

    List<VodCategory> findAllByOrderByDisplayOrderAsc();

    List<VodCategory> findByParentId(Long parentId);

    List<VodCategory> findByParentIdIsNull();



    Optional<VodCategory> findByCategoryName(String categoryName);
}
