package com.example.ormlearning.web;

import com.example.ormlearning.dto.CategoryRequest;
import com.example.ormlearning.dto.CategoryResponse;
import com.example.ormlearning.entity.Category;
import com.example.ormlearning.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Course categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a category")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.from(categoryService.create(category)));
    }

    @GetMapping
    @Operation(summary = "List categories")
    public List<CategoryResponse> list() {
        return categoryService.getAll().stream().map(CategoryResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by id")
    public CategoryResponse get(@PathVariable Long id) {
        return CategoryResponse.from(categoryService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}