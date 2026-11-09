package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Tag;

public record TagResponse(
        Long id,
        String name
) {
    public static TagResponse from(Tag t) {
        return new TagResponse(t.getId(), t.getName());
    }
}