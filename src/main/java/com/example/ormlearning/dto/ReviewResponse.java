package com.example.ormlearning.dto;

import com.example.ormlearning.entity.CourseReview;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long courseId,
        Long studentId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(CourseReview r) {
        return new ReviewResponse(
                r.getId(),
                r.getCourse() != null ? r.getCourse().getId() : null,
                r.getStudent() != null ? r.getStudent().getId() : null,
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}