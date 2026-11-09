package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Assignment;

import java.time.LocalDateTime;

public record AssignmentResponse(
        Long id,
        String title,
        String description,
        LocalDateTime dueDate,
        Integer maxScore,
        Long lessonId
) {
    public static AssignmentResponse from(Assignment a) {
        return new AssignmentResponse(
                a.getId(),
                a.getTitle(),
                a.getDescription(),
                a.getDueDate(),
                a.getMaxScore(),
                a.getLesson() != null ? a.getLesson().getId() : null
        );
    }
}