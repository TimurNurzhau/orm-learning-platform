package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Module;

public record ModuleResponse(
        Long id,
        String title,
        String description,
        Integer orderIndex,
        Long courseId
) {
    public static ModuleResponse from(Module m) {
        return new ModuleResponse(
                m.getId(),
                m.getTitle(),
                m.getDescription(),
                m.getOrderIndex(),
                m.getCourse() != null ? m.getCourse().getId() : null
        );
    }
}