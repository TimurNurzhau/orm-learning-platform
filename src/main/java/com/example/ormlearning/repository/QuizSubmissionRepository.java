package com.example.ormlearning.repository;

import com.example.ormlearning.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    List<QuizSubmission> findByQuizId(Long quizId);

    List<QuizSubmission> findByStudentId(Long studentId);
}