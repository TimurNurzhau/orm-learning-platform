package com.example.ormlearning.service;

import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Enrollment;
import com.example.ormlearning.entity.EnrollmentStatus;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.BusinessException;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.EnrollmentRepository;
import com.example.ormlearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public Enrollment enroll(Long studentId, Long courseId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new DuplicateResourceException(
                    "Student id=" + studentId + " already enrolled in course id=" + courseId);
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User (student)", studentId));
        if (student.getRole() != Role.STUDENT) {
            throw new BusinessException("User id=" + studentId + " is not a STUDENT");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollDate(LocalDateTime.now())
                .status(EnrollmentStatus.ACTIVE)
                .build();
        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment updateStatus(Long enrollmentId, EnrollmentStatus status) {
        Enrollment enrollment = getById(enrollmentId);
        enrollment.setStatus(status);
        return enrollment;
    }

    public Enrollment getById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", id));
    }

    public List<Enrollment> getByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Transactional
    public void unenroll(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment for student id=" + studentId + " and course id=" + courseId + " not found"));
        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new BusinessException("Cannot unenroll from completed course");
        }
        enrollmentRepository.delete(enrollment);
    }
}