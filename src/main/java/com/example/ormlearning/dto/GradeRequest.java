package com.example.ormlearning.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record GradeRequest(
        @Min(0) Integer score,
        @Size(max = 2000) String feedback
) {}