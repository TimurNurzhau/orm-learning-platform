package com.example.ormlearning.service;

import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.BusinessException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.CategoryRepository;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Course create(Course course, Long categoryId, Long teacherId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        course.setCategory(category);
        if (teacherId != null) {
            User teacher = userRepository.findById(teacherId)
                    .orElseThrow(() -> new ResourceNotFoundException("User (teacher)", teacherId));
            if (teacher.getRole() != com.example.ormlearning.entity.Role.TEACHER
                    && teacher.getRole() != com.example.ormlearning.entity.Role.ADMIN) {
                throw new BusinessException("User id=" + teacherId + " is not a TEACHER");
            }
            course.setTeacher(teacher);
        }
        return courseRepository.save(course);
    }

    public Course getById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    public Course getWithModules(Long id) {
        return courseRepository.findWithModulesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public List<Course> getByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    public List<Course> getByCategory(Long categoryId) {
        return courseRepository.findByCategoryId(categoryId);
    }

    @Transactional
    public Course update(Long id, Course updated, Long categoryId, Long teacherId) {
        Course existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setDurationHours(updated.getDurationHours());
        existing.setStartDate(updated.getStartDate());

        if (categoryId != null && !categoryId.equals(existing.getCategory().getId())) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
            existing.setCategory(category);
        }
        if (teacherId != null) {
            User teacher = userRepository.findById(teacherId)
                    .orElseThrow(() -> new ResourceNotFoundException("User (teacher)", teacherId));
            existing.setTeacher(teacher);
        }
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        Course course = getById(id);
        courseRepository.delete(course);
    }
}