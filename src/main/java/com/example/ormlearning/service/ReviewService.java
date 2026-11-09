package com.example.ormlearning.service;

import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.CourseReview;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.CourseReviewRepository;
import com.example.ormlearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final CourseReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public CourseReview create(Long studentId, Long courseId, CourseReview review) {
        if (reviewRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new DuplicateResourceException("Student already reviewed this course");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User (student)", studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        review.setStudent(student);
        review.setCourse(course);
        return reviewRepository.save(review);
    }

    public List<CourseReview> getByCourse(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }

    public Double averageRating(Long courseId) {
        return reviewRepository.averageRatingForCourse(courseId);
    }
}