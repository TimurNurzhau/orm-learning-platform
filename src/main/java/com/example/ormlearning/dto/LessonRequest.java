package com.example.ormlearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record LessonRequest(
        @NotBlank @Size(max = 200) String title,
        String content,
        @Size(max = 500) String videoUrl,
        @NotNull @PositiveOrZero Integer orderIndex,
        @NotNull Long moduleId
) {}