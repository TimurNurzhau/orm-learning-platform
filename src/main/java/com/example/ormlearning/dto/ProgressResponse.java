package com.example.ormlearning.dto;

import com.example.ormlearning.entity.LessonProgress;
import java.time.LocalDateTime;

public record ProgressResponse(
        Long id, Long userId, Long lessonId, String lessonTitle,
        boolean completed, LocalDateTime completedAt
) {
    public static ProgressResponse from(LessonProgress p) {
        return new ProgressResponse(
                p.getId(),
                p.getUser() != null ? p.getUser().getId() : null,
                p.getLesson() != null ? p.getLesson().getId() : null,
                p.getLesson() != null ? p.getLesson().getTitle() : null,
                p.isCompleted(),
                p.getCompletedAt()
        );
    }
}
