package com.example.ormlearning.repository;

import com.example.ormlearning.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<CourseReview> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<CourseReview> findByCourseId(Long courseId);

    List<CourseReview> findByStudentId(Long studentId);

    @Query("select avg(r.rating) from CourseReview r where r.course.id = :courseId")
    Double averageRatingForCourse(@Param("courseId") Long courseId);
}