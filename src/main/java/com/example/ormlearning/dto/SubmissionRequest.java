package com.example.ormlearning.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmissionRequest(
        @NotBlank String content,
        @jakarta.validation.constraints.NotNull Long studentId
) {}