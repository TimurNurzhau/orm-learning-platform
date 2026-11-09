package com.example.ormlearning.service;

import com.example.ormlearning.AbstractIntegrationTest;
import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.CourseReview;
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

class CourseReviewServiceIT extends AbstractIntegrationTest {

    @Autowired private ReviewService reviewService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CourseRepository courseRepository;

    @Test
    void shouldCreateReviewAndPreventDuplicates() {
        User student = userRepository.save(User.builder()
                .name("R").email("r@example.com").role(Role.STUDENT).build());
        Category cat = categoryRepository.save(Category.builder().name("RCat").build());
        Course course = courseRepository.save(Course.builder().title("RC").category(cat).build());

        CourseReview review = reviewService.create(student.getId(), course.getId(),
                CourseReview.builder().rating(5).comment("Great!").build());
        assertThat(review.getId()).isNotNull();

        assertThatThrownBy(() -> reviewService.create(student.getId(), course.getId(),
                CourseReview.builder().rating(4).build()))
                .isInstanceOf(DuplicateResourceException.class);

        Double avg = reviewService.averageRating(course.getId());
        assertThat(avg).isEqualTo(5.0);
    }
}
