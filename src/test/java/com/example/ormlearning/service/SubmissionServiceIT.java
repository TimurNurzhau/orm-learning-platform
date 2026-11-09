package com.example.ormlearning.service;

import com.example.ormlearning.AbstractIntegrationTest;
import com.example.ormlearning.entity.Assignment;
import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Lesson;
import com.example.ormlearning.entity.Module;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.Submission;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.repository.AssignmentRepository;
import com.example.ormlearning.repository.CategoryRepository;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.LessonRepository;
import com.example.ormlearning.repository.ModuleRepository;
import com.example.ormlearning.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubmissionServiceIT extends AbstractIntegrationTest {

    @Autowired private SubmissionService submissionService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ModuleRepository moduleRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private AssignmentRepository assignmentRepository;

    @Test
    void shouldSubmitAndGradeAssignment() {
        User student = userRepository.save(User.builder()
                .name("S").email("s@example.com").role(Role.STUDENT).build());
        Category cat = categoryRepository.save(Category.builder().name("C").build());
        Course course = courseRepository.save(Course.builder()
                .title("C").category(cat).build());
        Module module = moduleRepository.save(Module.builder()
                .title("M").orderIndex(1).course(course).build());
        Lesson lesson = lessonRepository.save(Lesson.builder()
                .title("L").orderIndex(1).module(module).build());
        Assignment assignment = assignmentRepository.save(Assignment.builder()
                .title("A").maxScore(100)
                .dueDate(LocalDateTime.now().plusDays(7))
                .lesson(lesson).build());

        Submission submission = submissionService.submit(student.getId(), assignment.getId(), "My solution");
        assertThat(submission.getId()).isNotNull();

        Submission graded = submissionService.grade(submission.getId(), 90, "Good job");
        assertThat(graded.getScore()).isEqualTo(90);
        assertThat(graded.getFeedback()).isEqualTo("Good job");

        assertThatThrownBy(() ->
                submissionService.submit(student.getId(), assignment.getId(), "Again"))
                .isInstanceOf(DuplicateResourceException.class);
    }
}
