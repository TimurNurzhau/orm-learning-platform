package com.example.ormlearning.service;

import com.example.ormlearning.entity.Assignment;
import com.example.ormlearning.entity.Submission;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.BusinessException;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.AssignmentRepository;
import com.example.ormlearning.repository.SubmissionRepository;
import com.example.ormlearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Submission submit(Long studentId, Long assignmentId, String content) {
        if (submissionRepository.existsByStudentIdAndAssignmentId(studentId, assignmentId)) {
            throw new DuplicateResourceException(
                    "Student id=" + studentId + " already submitted for assignment id=" + assignmentId);
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User (student)", studentId));
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", assignmentId));

        if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new BusinessException("Assignment due date passed");
        }

        Submission submission = Submission.builder()
                .student(student)
                .assignment(assignment)
                .content(content)
                .submittedAt(LocalDateTime.now())
                .build();
        return submissionRepository.save(submission);
    }

    @Transactional
    public Submission grade(Long submissionId, Integer score, String feedback) {
        Submission submission = getById(submissionId);
        if (score != null) {
            if (score < 0 || score > submission.getAssignment().getMaxScore()) {
                throw new BusinessException("Score must be between 0 and " + submission.getAssignment().getMaxScore());
            }
            submission.setScore(score);
        }
        if (feedback != null) submission.setFeedback(feedback);
        return submission;
    }

    public Submission getById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", id));
    }

    public List<Submission> getByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public List<Submission> getByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }
}