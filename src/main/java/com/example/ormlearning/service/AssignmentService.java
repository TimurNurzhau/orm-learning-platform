package com.example.ormlearning.service;

import com.example.ormlearning.entity.Assignment;
import com.example.ormlearning.entity.Lesson;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.AssignmentRepository;
import com.example.ormlearning.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public Assignment create(Assignment assignment, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));
        assignment.setLesson(lesson);
        return assignmentRepository.save(assignment);
    }

    public Assignment getById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", id));
    }

    public List<Assignment> getByLesson(Long lessonId) {
        return assignmentRepository.findByLessonId(lessonId);
    }

    @Transactional
    public Assignment update(Long id, Assignment updated) {
        Assignment existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setDueDate(updated.getDueDate());
        existing.setMaxScore(updated.getMaxScore());
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        assignmentRepository.delete(getById(id));
    }
}