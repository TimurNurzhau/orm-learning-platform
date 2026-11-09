package com.example.ormlearning.service;

import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.BusinessException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.CategoryRepository;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock private CourseRepository courseRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CourseService courseService;

    @Test
    void create_shouldAttachCategoryAndTeacher() {
        Category category = Category.builder().id(1L).name("Programming").build();
        User teacher = User.builder().id(2L).name("Teacher").role(Role.TEACHER).build();
        Course input = Course.builder().title("Hibernate").build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(2L)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            Course c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        Course saved = courseService.create(input, 1L, 2L);

        assertThat(saved.getId()).isEqualTo(10L);
        assertThat(saved.getCategory()).isSameAs(category);
        assertThat(saved.getTeacher()).isSameAs(teacher);
        verify(courseRepository).save(input);
    }

    @Test
    void create_shouldThrowWhenCategoryMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.create(Course.builder().title("X").build(), 99L, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category with id=99");

        verify(courseRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowWhenTeacherIsNotTeacher() {
        Category category = Category.builder().id(1L).build();
        User student = User.builder().id(5L).role(Role.STUDENT).build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(5L)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> courseService.create(Course.builder().title("X").build(), 1L, 5L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not a TEACHER");

        verify(courseRepository, never()).save(any());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(courseRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getById(42L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}