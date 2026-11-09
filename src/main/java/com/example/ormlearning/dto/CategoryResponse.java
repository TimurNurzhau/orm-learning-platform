package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Category;

public record CategoryResponse(
        Long id,
        String name,
        String description
) {
    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }
}