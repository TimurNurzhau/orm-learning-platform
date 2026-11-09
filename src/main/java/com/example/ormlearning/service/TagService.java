package com.example.ormlearning.service;

import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Tag;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Tag create(Tag tag) {
        if (tagRepository.existsByName(tag.getName())) {
            throw new DuplicateResourceException("Tag with name=" + tag.getName() + " already exists");
        }
        return tagRepository.save(tag);
    }

    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    public Tag getById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }

    @Transactional
    public void attachToCourse(Long courseId, Long tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        Tag tag = getById(tagId);
        course.getTags().add(tag);
    }

    @Transactional
    public void detachFromCourse(Long courseId, Long tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        Tag tag = getById(tagId);
        course.getTags().remove(tag);
    }

    @Transactional
    public void delete(Long id) {
        tagRepository.delete(getById(id));
    }
}