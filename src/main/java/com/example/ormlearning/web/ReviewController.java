package com.example.ormlearning.web;

import com.example.ormlearning.dto.ReviewRequest;
import com.example.ormlearning.dto.ReviewResponse;
import com.example.ormlearning.entity.CourseReview;
import com.example.ormlearning.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Course reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Leave a review for a course")
    public ResponseEntity<ReviewResponse> create(@PathVariable Long courseId,
                                                 @RequestParam Long studentId,
                                                 @Valid @RequestBody ReviewRequest request) {
        CourseReview review = CourseReview.builder()
                .rating(request.rating())
                .comment(request.comment())
                .build();
        CourseReview saved = reviewService.create(studentId, courseId, review);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.from(saved));
    }

    @GetMapping
    @Operation(summary = "List reviews for a course")
    public List<ReviewResponse> list(@PathVariable Long courseId) {
        return reviewService.getByCourse(courseId).stream().map(ReviewResponse::from).toList();
    }

    @GetMapping("/average")
    @Operation(summary = "Average rating for a course")
    public Double average(@PathVariable Long courseId) {
        return reviewService.averageRating(courseId);
    }
}