package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Enrollment;
import com.example.ormlearning.entity.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseTitle,
        LocalDateTime enrollDate,
        EnrollmentStatus status
) {
    public static EnrollmentResponse from(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getStudent() != null ? e.getStudent().getId() : null,
                e.getStudent() != null ? e.getStudent().getName() : null,
                e.getCourse() != null ? e.getCourse().getId() : null,
                e.getCourse() != null ? e.getCourse().getTitle() : null,
                e.getEnrollDate(),
                e.getStatus()
        );
    }
}