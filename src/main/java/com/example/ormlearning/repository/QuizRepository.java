package com.example.ormlearning.repository;

import com.example.ormlearning.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByModuleId(Long moduleId);
}