package com.example.ormlearning.web;

import com.example.ormlearning.dto.CourseCreateRequest;
import com.example.ormlearning.dto.CourseDetailResponse;
import com.example.ormlearning.dto.CourseResponse;
import com.example.ormlearning.dto.EnrollmentResponse;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Enrollment;
import com.example.ormlearning.service.CourseService;
import com.example.ormlearning.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management and enrollment")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Create a course")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseCreateRequest request) {
        Course course = Course.builder()
                .title(request.title()).description(request.description())
                .durationHours(request.durationHours()).startDate(request.startDate())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CourseResponse.from(courseService.create(course, request.categoryId(), request.teacherId())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a course")
    public CourseResponse update(@PathVariable Long id, @Valid @RequestBody CourseCreateRequest request) {
        Course patch = Course.builder()
                .title(request.title()).description(request.description())
                .durationHours(request.durationHours()).startDate(request.startDate())
                .build();
        return CourseResponse.from(courseService.update(id, patch, request.categoryId(), request.teacherId()));
    }

    @GetMapping
    @Operation(summary = "List courses, optional category or teacher filter")
    public List<CourseResponse> list(@RequestParam(required = false) Long categoryId,
                                     @RequestParam(required = false) Long teacherId) {
        if (categoryId != null) return courseService.getByCategory(categoryId).stream().map(CourseResponse::from).toList();
        if (teacherId != null) return courseService.getByTeacher(teacherId).stream().map(CourseResponse::from).toList();
        return courseService.getAll().stream().map(CourseResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a course by id")
    public CourseResponse get(@PathVariable Long id) {
        return CourseResponse.from(courseService.getById(id));
    }

    @GetMapping("/{id}/full")
    @Operation(summary = "Get a course with modules (eager via @EntityGraph)")
    public CourseDetailResponse getFull(@PathVariable Long id) {
        return CourseDetailResponse.from(courseService.getWithModules(id));
    }

    @PostMapping("/{courseId}/enroll/{studentId}")
    @Operation(summary = "Enroll a student in a course")
    public ResponseEntity<EnrollmentResponse> enroll(@PathVariable Long courseId, @PathVariable Long studentId) {
        Enrollment e = enrollmentService.enroll(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(EnrollmentResponse.from(e));
    }

    @DeleteMapping("/{courseId}/enroll/{studentId}")
    @Operation(summary = "Unenroll a student from a course")
    public ResponseEntity<Void> unenroll(@PathVariable Long courseId, @PathVariable Long studentId) {
        enrollmentService.unenroll(studentId, courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}/students")
    @Operation(summary = "List students enrolled in a course")
    public List<EnrollmentResponse> students(@PathVariable Long courseId) {
        return enrollmentService.getByCourse(courseId).stream().map(EnrollmentResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
