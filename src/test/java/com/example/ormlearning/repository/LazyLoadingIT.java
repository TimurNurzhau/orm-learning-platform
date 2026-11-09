package com.example.ormlearning.repository;

import com.example.ormlearning.AbstractIntegrationTest;
import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Module;
import com.example.ormlearning.repository.CategoryRepository;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.ModuleRepository;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LazyLoadingIT extends AbstractIntegrationTest {

    @Autowired private CourseRepository courseRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ModuleRepository moduleRepository;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowLazyInitializationExceptionOutsideTransaction() {
        Long courseId;
        {
            // отдельная транзакция через сам репозиторий (save — в своей транзакции)
            Category cat = categoryRepository.save(Category.builder().name("LazyCat").build());
            Course course = courseRepository.save(Course.builder().title("LazyCourse").category(cat).build());
            moduleRepository.save(Module.builder().title("M1").orderIndex(1).course(course).build());
            courseId = course.getId();
        }
        // сессия закрыта — обращаемся к LAZY-коллекции
        Course loaded = courseRepository.findById(courseId).orElseThrow();
        assertThatThrownBy(() -> loaded.getModules().size())
                .isInstanceOf(LazyInitializationException.class);
    }
}
