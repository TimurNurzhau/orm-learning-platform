package com.example.ormlearning.web;

import com.example.ormlearning.dto.AssignmentRequest;
import com.example.ormlearning.dto.AssignmentResponse;
import com.example.ormlearning.entity.Assignment;
import com.example.ormlearning.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Assignments", description = "Assignment management")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @Operation(summary = "Create an assignment")
    public ResponseEntity<AssignmentResponse> create(@Valid @RequestBody AssignmentRequest request) {
        Assignment assignment = Assignment.builder()
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .maxScore(request.maxScore())
                .build();
        Assignment saved = assignmentService.create(assignment, request.lessonId());
        return ResponseEntity.status(HttpStatus.CREATED).body(AssignmentResponse.from(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an assignment by id")
    public AssignmentResponse get(@PathVariable Long id) {
        return AssignmentResponse.from(assignmentService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List assignments by lesson")
    public List<AssignmentResponse> listByLesson(@RequestParam Long lessonId) {
        return assignmentService.getByLesson(lessonId).stream().map(AssignmentResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an assignment")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}