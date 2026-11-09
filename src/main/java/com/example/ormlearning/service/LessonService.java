package com.example.ormlearning.service;

import com.example.ormlearning.entity.Lesson;
import com.example.ormlearning.entity.Module;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.LessonRepository;
import com.example.ormlearning.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    @Transactional
    public Lesson create(Lesson lesson, Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));
        lesson.setModule(module);
        return lessonRepository.save(lesson);
    }

    public Lesson getById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", id));
    }

    public List<Lesson> getByModule(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
    }
    @Transactional
    public Lesson update(Long id, Lesson updated) {
        Lesson existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setContent(updated.getContent());
        existing.setVideoUrl(updated.getVideoUrl());
        existing.setOrderIndex(updated.getOrderIndex());
        return existing;
    }


    @Transactional
    public void delete(Long id) {
        lessonRepository.delete(getById(id));
    }
}
