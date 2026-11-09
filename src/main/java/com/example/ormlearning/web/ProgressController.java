package com.example.ormlearning.web;

import com.example.ormlearning.dto.ProgressResponse;
import com.example.ormlearning.service.LessonProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "Lesson progress tracking")
public class ProgressController {

    private final LessonProgressService progressService;

    @PostMapping("/users/{userId}/lessons/{lessonId}/complete")
    @Operation(summary = "Mark lesson as completed")
    public ProgressResponse complete(@PathVariable Long userId, @PathVariable Long lessonId) {
        return ProgressResponse.from(progressService.markCompleted(userId, lessonId));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "List progress for a user")
    public List<ProgressResponse> list(@PathVariable Long userId) {
        return progressService.getByUser(userId).stream().map(ProgressResponse::from).toList();
    }

    @GetMapping("/users/{userId}/completed-count")
    @Operation(summary = "Count completed lessons for a user")
    public long completedCount(@PathVariable Long userId) {
        return progressService.countCompletedByUser(userId);
    }
}
