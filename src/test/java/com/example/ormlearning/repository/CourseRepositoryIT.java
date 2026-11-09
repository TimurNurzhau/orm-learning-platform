package com.example.ormlearning.repository;

import com.example.ormlearning.AbstractIntegrationTest;
import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class CourseRepositoryIT extends AbstractIntegrationTest {

    @Autowired private CourseRepository courseRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void shouldSaveCourseWithCategoryAndTeacher() {
        Category category = categoryRepository.save(Category.builder().name("Programming").build());
        User teacher = userRepository.save(User.builder()
                .name("Teacher")
                .email("teacher@example.com")
                .role(Role.TEACHER)
                .build());

        Course course = courseRepository.save(Course.builder()
                .title("Hibernate Basics")
                .description("Intro to ORM")
                .category(category)
                .teacher(teacher)
                .build());

        assertThat(course.getId()).isNotNull();
        assertThat(courseRepository.findByTeacherId(teacher.getId())).hasSize(1);
        assertThat(courseRepository.findByCategoryId(category.getId())).hasSize(1);
    }
}