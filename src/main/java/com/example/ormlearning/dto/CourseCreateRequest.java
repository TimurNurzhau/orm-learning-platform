package com.example.ormlearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CourseCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        Integer durationHours,
        LocalDate startDate,
        @NotNull Long categoryId,
        Long teacherId
) {}