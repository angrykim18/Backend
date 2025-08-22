package com.newez.backend.controller;

import com.newez.backend.dto.CategoryDto;
import com.newez.backend.service.VodCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final VodCategoryService vodCategoryService;

    @Autowired
    public MovieController(VodCategoryService vodCategoryService) {
        this.vodCategoryService = vodCategoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getMovieCategories() {

        List<CategoryDto> categories = vodCategoryService.findMovieSubCategories();

        return ResponseEntity.ok(categories);
    }
}