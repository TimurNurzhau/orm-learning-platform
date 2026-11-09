package com.example.ormlearning.service;

import com.example.ormlearning.AbstractIntegrationTest;
import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Enrollment;
import com.example.ormlearning.entity.EnrollmentStatus;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.repository.CategoryRepository;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnrollmentServiceIT extends AbstractIntegrationTest {

    @Autowired private EnrollmentService enrollmentService;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Test
    void shouldEnrollStudentAndPreventDuplicates() {
        User student = userRepository.save(User.builder()
                .name("Student").email("s1@example.com").role(Role.STUDENT).build());
        Category cat = categoryRepository.save(Category.builder().name("Cat1").build());
        Course course = courseRepository.save(Course.builder()
                .title("C1").category(cat).build());

        Enrollment enrollment = enrollmentService.enroll(student.getId(), course.getId());

        assertThat(enrollment.getId()).isNotNull();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);

        assertThatThrownBy(() -> enrollmentService.enroll(student.getId(), course.getId()))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void shouldUnenroll() {
        User student = userRepository.save(User.builder()
                .name("Student2").email("s2@example.com").role(Role.STUDENT).build());
        Category cat = categoryRepository.save(Category.builder().name("Cat2").build());
        Course course = courseRepository.save(Course.builder()
                .title("C2").category(cat).build());

        enrollmentService.enroll(student.getId(), course.getId());
        enrollmentService.unenroll(student.getId(), course.getId());

        assertThat(enrollmentService.getByStudent(student.getId())).isEmpty();
    }
}
