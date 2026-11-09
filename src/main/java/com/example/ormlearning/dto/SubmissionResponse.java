package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Submission;

import java.time.LocalDateTime;

public record SubmissionResponse(
        Long id,
        Long assignmentId,
        Long studentId,
        String studentName,
        String content,
        Integer score,
        String feedback,
        LocalDateTime submittedAt
) {
    public static SubmissionResponse from(Submission s) {
        return new SubmissionResponse(
                s.getId(),
                s.getAssignment() != null ? s.getAssignment().getId() : null,
                s.getStudent() != null ? s.getStudent().getId() : null,
                s.getStudent() != null ? s.getStudent().getName() : null,
                s.getContent(),
                s.getScore(),
                s.getFeedback(),
                s.getSubmittedAt()
        );
    }
}