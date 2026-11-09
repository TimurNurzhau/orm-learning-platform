package com.example.ormlearning.repository;

import com.example.ormlearning.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizId(Long quizId);

    /**
     * Loads questions with their answer options eagerly.
     * Used as a second query after Quiz is loaded with questions.
     */
    @EntityGraph(attributePaths = {"options"})
    List<Question> findWithOptionsByQuizId(Long quizId);
}