package com.example.ormlearning.dto;

import com.example.ormlearning.entity.QuizSubmission;

import java.time.LocalDateTime;

public record QuizSubmissionResponse(
        Long id,
        Long quizId,
        Long studentId,
        Integer score,
        Integer maxScore,
        boolean passed,
        LocalDateTime takenAt
) {
    public static QuizSubmissionResponse from(QuizSubmission q) {
        return new QuizSubmissionResponse(
                q.getId(),
                q.getQuiz() != null ? q.getQuiz().getId() : null,
                q.getStudent() != null ? q.getStudent().getId() : null,
                q.getScore(),
                q.getMaxScore(),
                q.isPassed(),
                q.getTakenAt()
        );
    }
}