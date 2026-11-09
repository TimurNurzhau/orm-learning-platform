package com.example.ormlearning.service;

import com.example.ormlearning.entity.Assignment;
import com.example.ormlearning.entity.Lesson;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.Submission;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.BusinessException;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.AssignmentRepository;
import com.example.ormlearning.repository.SubmissionRepository;
import com.example.ormlearning.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock private SubmissionRepository submissionRepository;
    @Mock private AssignmentRepository assignmentRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private SubmissionService submissionService;

    private static User student() {
        return User.builder().id(1L).name("Alice").role(Role.STUDENT).build();
    }

    private static Assignment assignment(LocalDateTime dueDate, int maxScore) {
        return Assignment.builder()
                .id(10L).title("HW").maxScore(maxScore).dueDate(dueDate)
                .lesson(Lesson.builder().id(1L).title("L").build())
                .build();
    }

    @Test
    void submit_shouldCreateSubmission() {
        User student = student();
        Assignment assignment = assignment(LocalDateTime.now().plusDays(3), 100);

        when(submissionRepository.existsByStudentIdAndAssignmentId(1L, 10L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(assignmentRepository.findById(10L)).thenReturn(Optional.of(assignment));
        when(submissionRepository.save(any(Submission.class))).thenAnswer(inv -> {
            Submission s = inv.getArgument(0);
            s.setId(100L);
            return s;
        });

        Submission result = submissionService.submit(1L, 10L, "my solution");

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getContent()).isEqualTo("my solution");
        assertThat(result.getStudent()).isSameAs(student);
        assertThat(result.getAssignment()).isSameAs(assignment);
    }

    @Test
    void submit_shouldThrowOnDuplicate() {
        when(submissionRepository.existsByStudentIdAndAssignmentId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> submissionService.submit(1L, 10L, "again"))
                .isInstanceOf(DuplicateResourceException.class);

        verifyNoInteractions(userRepository, assignmentRepository);
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void submit_shouldThrowWhenDueDatePassed() {
        Assignment expired = assignment(LocalDateTime.now().minusDays(1), 100);

        when(submissionRepository.existsByStudentIdAndAssignmentId(1L, 10L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student()));
        when(assignmentRepository.findById(10L)).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> submissionService.submit(1L, 10L, "late"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("due date passed");
    }

    @Test
    void grade_shouldThrowWhenScoreOutOfRange() {
        Submission submission = Submission.builder()
                .id(100L)
                .assignment(assignment(LocalDateTime.now().plusDays(1), 50))
                .build();
        when(submissionRepository.findById(100L)).thenReturn(Optional.of(submission));

        assertThatThrownBy(() -> submissionService.grade(100L, 60, "ok"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("between 0 and 50");
    }

    @Test
    void grade_shouldSetScoreAndFeedback() {
        Submission submission = Submission.builder()
                .id(100L)
                .assignment(assignment(LocalDateTime.now().plusDays(1), 100))
                .build();
        when(submissionRepository.findById(100L)).thenReturn(Optional.of(submission));

        Submission graded = submissionService.grade(100L, 90, "good");

        assertThat(graded.getScore()).isEqualTo(90);
        assertThat(graded.getFeedback()).isEqualTo("good");
    }

    @Test
    void getById_shouldThrowWhenMissing() {
        when(submissionRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.getById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}