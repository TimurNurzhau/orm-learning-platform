package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Course;
import java.util.List;

public record CourseDetailResponse(
        Long id, String title, String description, Integer durationHours,
        Long categoryId, String categoryName, Long teacherId, String teacherName,
        List<ModuleResponse> modules
) {
    public static CourseDetailResponse from(Course c) {
        return new CourseDetailResponse(
                c.getId(), c.getTitle(), c.getDescription(), c.getDurationHours(),
                c.getCategory() != null ? c.getCategory().getId() : null,
                c.getCategory() != null ? c.getCategory().getName() : null,
                c.getTeacher() != null ? c.getTeacher().getId() : null,
                c.getTeacher() != null ? c.getTeacher().getName() : null,
                c.getModules().stream().map(ModuleResponse::from).toList()
        );
    }
}
