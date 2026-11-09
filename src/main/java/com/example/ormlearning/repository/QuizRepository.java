package com.example.ormlearning.repository;

import com.example.ormlearning.entity.Quiz;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByModuleId(Long moduleId);

    /**
     * Loads Quiz + questions only (single collection — safe for EntityGraph).
     * Options are loaded separately via QuestionRepository to avoid
     * MultipleBagFetchException.
     */
    @EntityGraph(attributePaths = {"questions"})
    @Query("select q from Quiz q where q.id = :id")
    Optional<Quiz> findWithQuestionsById(@Param("id") Long id);
}