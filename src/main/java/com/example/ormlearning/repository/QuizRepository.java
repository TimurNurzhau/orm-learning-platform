package com.example.ormlearning.repository;

import com.example.ormlearning.entity.Quiz;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByModuleId(Long moduleId);

    /**
     * Loads a quiz together with its questions and their answer options
     * in a single query (avoids LazyInitializationException outside a transaction).
     */
    @EntityGraph(attributePaths = {"questions", "questions.options"})
    Optional<Quiz> findWithQuestionsById(Long id);
}