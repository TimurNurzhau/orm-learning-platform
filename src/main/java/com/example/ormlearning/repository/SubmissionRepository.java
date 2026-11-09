package com.example.ormlearning.repository;

import com.example.ormlearning.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    boolean existsByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    Optional<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStudentId(Long studentId);
}