package com.example.ormlearning.web;

import com.example.ormlearning.dto.QuizRequest;
import com.example.ormlearning.dto.QuizResponse;
import com.example.ormlearning.dto.QuizSubmissionResponse;
import com.example.ormlearning.dto.TakeQuizRequest;
import com.example.ormlearning.entity.Quiz;
import com.example.ormlearning.entity.QuizSubmission;
import com.example.ormlearning.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quizzes", description = "Quiz management and attempts")
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    @Operation(summary = "Create a quiz")
    public ResponseEntity<QuizResponse> create(@Valid @RequestBody QuizRequest request) {
        Quiz quiz = Quiz.builder()
                .title(request.title())
                .timeLimitMinutes(request.timeLimitMinutes())
                .build();
        Quiz saved = quizService.create(quiz, request.moduleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(QuizResponse.from(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a quiz by id")
    public QuizResponse get(@PathVariable Long id) {
        return QuizResponse.from(quizService.getById(id));
    }

    @PostMapping("/{quizId}/take")
    @Operation(summary = "Take a quiz and save the result")
    public ResponseEntity<QuizSubmissionResponse> take(@PathVariable Long quizId,
                                                       @Valid @RequestBody TakeQuizRequest request) {
        QuizSubmission result = quizService.takeQuiz(request.studentId(), quizId, request.selectedAnswers());
        return ResponseEntity.status(HttpStatus.CREATED).body(QuizSubmissionResponse.from(result));
    }

    @GetMapping("/students/{studentId}/results")
    @Operation(summary = "List quiz results for a student")
    public List<QuizSubmissionResponse> resultsByStudent(@PathVariable Long studentId) {
        return quizService.getResultsByStudent(studentId).stream().map(QuizSubmissionResponse::from).toList();
    }

    @GetMapping("/{quizId}/results")
    @Operation(summary = "List quiz results for a quiz")
    public List<QuizSubmissionResponse> resultsByQuiz(@PathVariable Long quizId) {
        return quizService.getResultsByQuiz(quizId).stream().map(QuizSubmissionResponse::from).toList();
    }
}