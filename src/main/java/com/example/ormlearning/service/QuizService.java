package com.example.ormlearning.service;

import com.example.ormlearning.entity.AnswerOption;
import com.example.ormlearning.entity.Module;
import com.example.ormlearning.entity.Question;
import com.example.ormlearning.entity.Quiz;
import com.example.ormlearning.entity.QuizSubmission;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.ModuleRepository;
import com.example.ormlearning.repository.QuizRepository;
import com.example.ormlearning.repository.QuizSubmissionRepository;
import com.example.ormlearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public Quiz create(Quiz quiz, Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));
        quiz.setModule(module);
        return quizRepository.save(quiz);
    }

    public Quiz getById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
    }

    /**
     * Returns a quiz with questions and options eagerly loaded via @EntityGraph.
     * Safe to call outside a transaction.
     */
    public Quiz getWithQuestions(Long id) {
        return quizRepository.findWithQuestionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
    }

    @Transactional
    public QuizSubmission takeQuiz(Long studentId, Long quizId, Map<Long, List<Long>> selectedAnswers) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User (student)", studentId));
        Quiz quiz = getWithQuestions(quizId);

        int earned = 0;
        int max = 0;
        for (Question question : quiz.getQuestions()) {
            int points = question.getPoints() == null ? 1 : question.getPoints();
            max += points;
            List<Long> selected = selectedAnswers.getOrDefault(question.getId(), List.of());
            List<Long> correctIds = question.getOptions().stream()
                    .filter(AnswerOption::isCorrect)
                    .map(AnswerOption::getId)
                    .toList();
            if (selected.size() == correctIds.size() && selected.containsAll(correctIds)) {
                earned += points;
            }
        }

        QuizSubmission submission = QuizSubmission.builder()
                .student(student)
                .quiz(quiz)
                .score(earned)
                .maxScore(max)
                .passed(max > 0 && (earned * 100 / max) >= 60)
                .takenAt(LocalDateTime.now())
                .build();
        return quizSubmissionRepository.save(submission);
    }

    public List<QuizSubmission> getResultsByStudent(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId);
    }

    public List<QuizSubmission> getResultsByQuiz(Long quizId) {
        return quizSubmissionRepository.findByQuizId(quizId);
    }
}