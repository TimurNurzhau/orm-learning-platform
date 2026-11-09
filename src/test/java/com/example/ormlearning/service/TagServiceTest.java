package com.example.ormlearning.service;

import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Tag;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.TagRepository;
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
class TagServiceTest {

    @Mock private TagRepository tagRepository;
    @Mock private CourseRepository courseRepository;

    @InjectMocks private TagService tagService;

    @Test
    void create_shouldThrowOnDuplicateName() {
        when(tagRepository.existsByName("Java")).thenReturn(true);

        assertThatThrownBy(() -> tagService.create(Tag.builder().name("Java").build()))
                .isInstanceOf(DuplicateResourceException.class);

        verify(tagRepository, never()).save(any());
    }

    @Test
    void attachToCourse_shouldAddTagToCourseTags() {
        Course course = Course.builder().id(1L).title("C")
                .category(Category.builder().id(1L).build()).build();
        Tag tag = Tag.builder().id(2L).name("Java").build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag));

        tagService.attachToCourse(1L, 2L);

        assertThat(course.getTags()).contains(tag);
    }

    @Test
    void detachFromCourse_shouldRemoveTag() {
        Course course = Course.builder().id(1L).title("C")
                .category(Category.builder().id(1L).build()).build();
        Tag tag = Tag.builder().id(2L).name("Java").build();
        course.getTags().add(tag);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag));

        tagService.detachFromCourse(1L, 2L);

        assertThat(course.getTags()).doesNotContain(tag);
    }

    @Test
    void attachToCourse_shouldThrowWhenCourseMissing() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.attachToCourse(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}