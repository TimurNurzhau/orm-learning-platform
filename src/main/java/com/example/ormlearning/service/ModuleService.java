package com.example.ormlearning.service;

import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Module;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Module create(Module module, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        module.setCourse(course);
        return moduleRepository.save(module);
    }

    public Module getById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", id));
    }

    public List<Module> getByCourse(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
    }
    @Transactional
    public Module update(Long id, Module updated) {
        Module existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setOrderIndex(updated.getOrderIndex());
        return existing;
    }


    @Transactional
    public void delete(Long id) {
        moduleRepository.delete(getById(id));
    }
}
