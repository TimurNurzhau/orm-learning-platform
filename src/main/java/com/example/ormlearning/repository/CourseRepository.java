package com.example.ormlearning.repository;

import com.example.ormlearning.entity.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTeacherId(Long teacherId);

    List<Course> findByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"modules", "category", "teacher"})
    Optional<Course> findWithModulesById(Long id);

    @EntityGraph(attributePaths = {"tags"})
    Optional<Course> findWithTagsById(Long id);
}