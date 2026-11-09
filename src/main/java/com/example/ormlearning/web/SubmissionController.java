package com.example.ormlearning.web;

import com.example.ormlearning.dto.GradeRequest;
import com.example.ormlearning.dto.SubmissionRequest;
import com.example.ormlearning.dto.SubmissionResponse;
import com.example.ormlearning.entity.Submission;
import com.example.ormlearning.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Assignment submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/assignments/{assignmentId}")
    @Operation(summary = "Submit an assignment")
    public ResponseEntity<SubmissionResponse> submit(@PathVariable Long assignmentId,
                                                     @Valid @RequestBody SubmissionRequest request) {
        Submission submission = submissionService.submit(request.studentId(), assignmentId, request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(SubmissionResponse.from(submission));
    }

    @PatchMapping("/{id}/grade")
    @Operation(summary = "Grade a submission")
    public SubmissionResponse grade(@PathVariable Long id, @Valid @RequestBody GradeRequest request) {
        return SubmissionResponse.from(submissionService.grade(id, request.score(), request.feedback()));
    }

    @GetMapping("/assignments/{assignmentId}")
    @Operation(summary = "List submissions for an assignment")
    public List<SubmissionResponse> listByAssignment(@PathVariable Long assignmentId) {
        return submissionService.getByAssignment(assignmentId).stream().map(SubmissionResponse::from).toList();
    }

    @GetMapping("/students/{studentId}")
    @Operation(summary = "List submissions by a student")
    public List<SubmissionResponse> listByStudent(@PathVariable Long studentId) {
        return submissionService.getByStudent(studentId).stream().map(SubmissionResponse::from).toList();
    }
}