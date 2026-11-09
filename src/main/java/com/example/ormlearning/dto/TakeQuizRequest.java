package com.example.ormlearning.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record TakeQuizRequest(
        @NotNull Long studentId,
        @NotNull Map<Long, List<Long>> selectedAnswers
) {}