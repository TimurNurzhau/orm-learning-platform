package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Lesson;

public record LessonResponse(
        Long id,
        String title,
        String content,
        String videoUrl,
        Integer orderIndex,
        Long moduleId
) {
    public static LessonResponse from(Lesson l) {
        return new LessonResponse(
                l.getId(),
                l.getTitle(),
                l.getContent(),
                l.getVideoUrl(),
                l.getOrderIndex(),
                l.getModule() != null ? l.getModule().getId() : null
        );
    }
}