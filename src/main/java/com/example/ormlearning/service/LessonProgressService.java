package com.example.ormlearning.service;

import com.example.ormlearning.entity.Lesson;
import com.example.ormlearning.entity.LessonProgress;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.LessonProgressRepository;
import com.example.ormlearning.repository.LessonRepository;
import com.example.ormlearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonProgressService {

    private final LessonProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Transactional
    public LessonProgress markCompleted(Long userId, Long lessonId) {
        LessonProgress progress = progressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
                    Lesson lesson = lessonRepository.findById(lessonId)
                            .orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));
                    return LessonProgress.builder()
                            .user(user)
                            .lesson(lesson)
                            .completed(false)
                            .build();
                });
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        return progressRepository.save(progress);
    }

    public List<LessonProgress> getByUser(Long userId) {
        return progressRepository.findByUserId(userId);
    }

    public long countCompletedByUser(Long userId) {
        return progressRepository.countByUserIdAndCompletedTrue(userId);
    }
}