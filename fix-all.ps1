# fix-all.ps1 — сохранить в корень проекта, запустить: powershell -ExecutionPolicy Bypass -File .\fix-all.ps1
$ErrorActionPreference = "Stop"
$root = "C:\Users\gerom\IdeaProjects\orm-learning-platform\orm-learning-platform"
Set-Location $root

# ---------- 1. DTO для прогресса ----------
@'
package com.example.ormlearning.dto;

import com.example.ormlearning.entity.LessonProgress;
import java.time.LocalDateTime;

public record ProgressResponse(
        Long id, Long userId, Long lessonId, String lessonTitle,
        boolean completed, LocalDateTime completedAt
) {
    public static ProgressResponse from(LessonProgress p) {
        return new ProgressResponse(
                p.getId(),
                p.getUser() != null ? p.getUser().getId() : null,
                p.getLesson() != null ? p.getLesson().getId() : null,
                p.getLesson() != null ? p.getLesson().getTitle() : null,
                p.isCompleted(),
                p.getCompletedAt()
        );
    }
}
'@ | Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\dto\ProgressResponse.java"

# ---------- 2. ProgressController на DTO ----------
@'
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
'@ | Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\web\ProgressController.java"

# ---------- 3. GlobalExceptionHandler с 3 новыми хендлерами ----------
@'
package com.example.ormlearning.web;

import com.example.ormlearning.exception.BusinessException;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ApiError(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            Map<String, String> details
    ) {}

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> notFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> duplicate(DuplicateResourceException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> dataIntegrity(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "Data integrity violation", null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> business(BusinessException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> details.put(fe.getField(), fe.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Validation failed", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> badJson(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> badParam(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid parameter: " + ex.getName(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> generic(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, Map<String, String> details) {
        return ResponseEntity.status(status).body(new ApiError(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, details));
    }
}
'@ | Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\web\GlobalExceptionHandler.java"

# ---------- 4. LazyInitializationException тест ----------
@'
package com.example.ormlearning.repository;

import com.example.ormlearning.AbstractIntegrationTest;
import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Module;
import com.example.ormlearning.repository.CategoryRepository;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.ModuleRepository;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LazyLoadingIT extends AbstractIntegrationTest {

    @Autowired private CourseRepository courseRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ModuleRepository moduleRepository;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowLazyInitializationExceptionOutsideTransaction() {
        Long courseId;
        {
            // отдельная транзакция через сам репозиторий (save — в своей транзакции)
            Category cat = categoryRepository.save(Category.builder().name("LazyCat").build());
            Course course = courseRepository.save(Course.builder().title("LazyCourse").category(cat).build());
            moduleRepository.save(Module.builder().title("M1").orderIndex(1).course(course).build());
            courseId = course.getId();
        }
        // сессия закрыта — обращаемся к LAZY-коллекции
        Course loaded = courseRepository.findById(courseId).orElseThrow();
        assertThatThrownBy(() -> loaded.getModules().size())
                .isInstanceOf(LazyInitializationException.class);
    }
}
'@ | Set-Content -Encoding UTF8 "src\test\java\com\example\ormlearning\repository\LazyLoadingIT.java"

# ---------- 5. CourseDetailResponse + endpoint ----------
@'
package com.example.ormlearning.dto;

import com.example.ormlearning.entity.Course;
import java.util.List;

public record CourseDetailResponse(
        Long id, String title, String description, Integer durationHours,
        Long categoryId, String categoryName, Long teacherId, String teacherName,
        List<ModuleResponse> modules
) {
    public static CourseDetailResponse from(Course c) {
        return new CourseDetailResponse(
                c.getId(), c.getTitle(), c.getDescription(), c.getDurationHours(),
                c.getCategory() != null ? c.getCategory().getId() : null,
                c.getCategory() != null ? c.getCategory().getName() : null,
                c.getTeacher() != null ? c.getTeacher().getId() : null,
                c.getTeacher() != null ? c.getTeacher().getName() : null,
                c.getModules().stream().map(ModuleResponse::from).toList()
        );
    }
}
'@ | Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\dto\CourseDetailResponse.java"

# ---------- 6. Обновлённый CourseController (PUT + full + unenroll) ----------
@'
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
'@ | Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\web\CourseController.java"

# ---------- 7. UserController + GET courses ----------
$user = Get-Content -Raw "src\main\java\com\example\ormlearning\web\UserController.java"
if ($user -notmatch 'getByStudent') {
    $user = $user -replace '(import com\.example\.ormlearning\.service\.UserService;)',
        "`$1`nimport com.example.ormlearning.dto.EnrollmentResponse;`nimport com.example.ormlearning.service.EnrollmentService;"
    $user = $user -replace 'private final UserService userService;',
        "private final UserService userService;`n    private final EnrollmentService enrollmentService;"
    $user = $user -replace '(\s*@DeleteMapping\("/\{id\}"\))',
        "`n    @GetMapping(`"/{id}/courses`")`n    @Operation(summary = `"List courses a student is enrolled in`")`n    public List<EnrollmentResponse> courses(@PathVariable Long id) {`n        return enrollmentService.getByStudent(id).stream().map(EnrollmentResponse::from).toList();`n    }`n`n`$1"
    Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\web\UserController.java" $user
}

# ---------- 8. ModuleService.update ----------
$ms = Get-Content -Raw "src\main\java\com\example\ormlearning\service\ModuleService.java"
if ($ms -notmatch 'public Module update') {
    $ms = $ms -replace '(\s*@Transactional\s+public void delete)',
        "`n    @Transactional`n    public Module update(Long id, Module updated) {`n        Module existing = getById(id);`n        existing.setTitle(updated.getTitle());`n        existing.setDescription(updated.getDescription());`n        existing.setOrderIndex(updated.getOrderIndex());`n        return existing;`n    }`n`$1"
    Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\service\ModuleService.java" $ms
}

# ---------- 9. LessonService.update ----------
$ls = Get-Content -Raw "src\main\java\com\example\ormlearning\service\LessonService.java"
if ($ls -notmatch 'public Lesson update') {
    $ls = $ls -replace '(\s*@Transactional\s+public void delete)',
        "`n    @Transactional`n    public Lesson update(Long id, Lesson updated) {`n        Lesson existing = getById(id);`n        existing.setTitle(updated.getTitle());`n        existing.setContent(updated.getContent());`n        existing.setVideoUrl(updated.getVideoUrl());`n        existing.setOrderIndex(updated.getOrderIndex());`n        return existing;`n    }`n`$1"
    Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\service\LessonService.java" $ls
}

# ---------- 10. PUT для module ----------
$mc = Get-Content -Raw "src\main\java\com\example\ormlearning\web\ModuleController.java"
if ($mc -notmatch 'PutMapping') {
    $mc = $mc -replace '(\s*@DeleteMapping\("/\{id\}"\))',
        "`n    @PutMapping(`"/{id}`")`n    @Operation(summary = `"Update a module`")`n    public ModuleResponse update(@PathVariable Long id, @Valid @RequestBody ModuleRequest request) {`n        Module patch = Module.builder().title(request.title()).description(request.description())`n                .orderIndex(request.orderIndex()).build();`n        return ModuleResponse.from(moduleService.update(id, patch));`n    }`n`$1"
    Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\web\ModuleController.java" $mc
}

# ---------- 11. PUT для lesson ----------
$lc = Get-Content -Raw "src\main\java\com\example\ormlearning\web\LessonController.java"
if ($lc -notmatch 'PutMapping') {
    $lc = $lc -replace '(\s*@DeleteMapping\("/\{id\}"\))',
        "`n    @PutMapping(`"/{id}`")`n    @Operation(summary = `"Update a lesson`")`n    public LessonResponse update(@PathVariable Long id, @Valid @RequestBody LessonRequest request) {`n        Lesson patch = Lesson.builder().title(request.title()).content(request.content())`n                .videoUrl(request.videoUrl()).orderIndex(request.orderIndex()).build();`n        return LessonResponse.from(lessonService.update(id, patch));`n    }`n`$1"
    Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\web\LessonController.java" $lc
}

# ---------- 12. PUT для assignment ----------
$ac = Get-Content -Raw "src\main\java\com\example\ormlearning\web\AssignmentController.java"
if ($ac -notmatch 'PutMapping') {
    $ac = $ac -replace '(\s*@DeleteMapping\("/\{id\}"\))',
        "`n    @PutMapping(`"/{id}`")`n    @Operation(summary = `"Update an assignment`")`n    public AssignmentResponse update(@PathVariable Long id, @Valid @RequestBody AssignmentRequest request) {`n        Assignment patch = Assignment.builder().title(request.title()).description(request.description())`n                .dueDate(request.dueDate()).maxScore(request.maxScore()).build();`n        return AssignmentResponse.from(assignmentService.update(id, patch));`n    }`n`$1"
    Set-Content -Encoding UTF8 "src\main\java\com\example\ormlearning\web\AssignmentController.java" $ac
}

# ---------- 13. datasource в application-dev.yml ----------
$dev = Get-Content -Raw "src\main\resources\application-dev.yml"
if ($dev -notmatch 'datasource') {
    $dev = $dev -replace '(spring:\s*\r?\n)', "`$1  datasource:`n    url: jdbc:postgresql://localhost:5432/learning_platform`n    username: postgres`n    password: postgres`n"
    Set-Content -Encoding UTF8 "src\main\resources\application-dev.yml" $dev
}

# ---------- 14. Сборка и тесты ----------
Write-Host "`n=== Running mvn clean test ===`n" -ForegroundColor Cyan
mvn -B -q clean test
if ($LASTEXITCODE -eq 0) {
    Write-Host "`n=== ALL TESTS GREEN — фиксы применены ===`n" -ForegroundColor Green
} else {
    Write-Host "`n=== TESTS FAILED — смотрите вывод выше ===`n" -ForegroundColor Red
    exit 1
}
