package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Quiz;

public record QuizResponse(
        Long id,
        String title,
        Integer timeLimitMinutes,
        Long moduleId
) {
    public static QuizResponse from(Quiz q) {
        return new QuizResponse(
                q.getId(),
                q.getTitle(),
                q.getTimeLimitMinutes(),
                q.getModule() != null ? q.getModule().getId() : null
        );
    }
}