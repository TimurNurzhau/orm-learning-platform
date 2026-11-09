package com.example.ormlearning.web;

import com.example.ormlearning.dto.LessonRequest;
import com.example.ormlearning.dto.LessonResponse;
import com.example.ormlearning.entity.Lesson;
import com.example.ormlearning.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Module lessons")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @Operation(summary = "Create a lesson")
    public ResponseEntity<LessonResponse> create(@Valid @RequestBody LessonRequest request) {
        Lesson lesson = Lesson.builder()
                .title(request.title())
                .content(request.content())
                .videoUrl(request.videoUrl())
                .orderIndex(request.orderIndex())
                .build();
        Lesson saved = lessonService.create(lesson, request.moduleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(LessonResponse.from(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a lesson by id")
    public LessonResponse get(@PathVariable Long id) {
        return LessonResponse.from(lessonService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List lessons by module")
    public List<LessonResponse> listByModule(@RequestParam Long moduleId) {
        return lessonService.getByModule(moduleId).stream().map(LessonResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a lesson")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}