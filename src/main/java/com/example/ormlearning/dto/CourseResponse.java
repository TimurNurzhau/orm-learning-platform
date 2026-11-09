package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Course;

import java.time.LocalDate;

public record CourseResponse(
        Long id,
        String title,
        String description,
        Integer durationHours,
        LocalDate startDate,
        Long categoryId,
        String categoryName,
        Long teacherId,
        String teacherName
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDurationHours(),
                course.getStartDate(),
                course.getCategory() != null ? course.getCategory().getId() : null,
                course.getCategory() != null ? course.getCategory().getName() : null,
                course.getTeacher() != null ? course.getTeacher().getId() : null,
                course.getTeacher() != null ? course.getTeacher().getName() : null
        );
    }
}