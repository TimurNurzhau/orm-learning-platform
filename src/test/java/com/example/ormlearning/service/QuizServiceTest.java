package com.example.ormlearning.service;

import com.example.ormlearning.entity.AnswerOption;
import com.example.ormlearning.entity.Module;
import com.example.ormlearning.entity.Question;
import com.example.ormlearning.entity.QuestionType;
import com.example.ormlearning.entity.Quiz;
import com.example.ormlearning.entity.QuizSubmission;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.ModuleRepository;
import com.example.ormlearning.repository.QuizRepository;
import com.example.ormlearning.repository.QuizSubmissionRepository;
import com.example.ormlearning.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock private QuizRepository quizRepository;
    @Mock private QuizSubmissionRepository quizSubmissionRepository;
    @Mock private ModuleRepository moduleRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private QuizService quizService;

    private static Quiz sampleQuiz() {
        Question q1 = Question.builder().id(1L).text("2+2?").type(QuestionType.SINGLE_CHOICE)
                .orderIndex(1).points(1).build();
        AnswerOption c1 = AnswerOption.builder().id(11L).text("4").correct(true).orderIndex(1).question(q1).build();
        AnswerOption w1 = AnswerOption.builder().id(12L).text("5").correct(false).orderIndex(2).question(q1).build();
        q1.setOptions(List.of(c1, w1));

        Question q2 = Question.builder().id(2L).text("2*3?").type(QuestionType.SINGLE_CHOICE)
                .orderIndex(2).points(1).build();
        AnswerOption c2 = AnswerOption.builder().id(21L).text("6").correct(true).orderIndex(1).question(q2).build();
        AnswerOption w2 = AnswerOption.builder().id(22L).text("7").correct(false).orderIndex(2).question(q2).build();
        q2.setOptions(List.of(c2, w2));

        Quiz quiz = Quiz.builder().id(100L).title("Basics")
                .module(Module.builder().id(1L).title("M").orderIndex(1).build())
                .build();
        quiz.setQuestions(List.of(q1, q2));
        return quiz;
    }

    @Test
    void takeQuiz_shouldPassWhenAllCorrect() {
        Quiz quiz = sampleQuiz();
        when(userRepository.findById(1L)).thenReturn(Optional.of(
                User.builder().id(1L).name("A").role(Role.STUDENT).build()));
        when(quizRepository.findWithQuestionsById(100L)).thenReturn(Optional.of(quiz));
        when(quizSubmissionRepository.save(any(QuizSubmission.class))).thenAnswer(inv -> {
            QuizSubmission s = inv.getArgument(0);
            s.setId(500L);
            return s;
        });

        QuizSubmission result = quizService.takeQuiz(1L, 100L,
                Map.of(1L, List.of(11L), 2L, List.of(21L)));

        assertThat(result.getScore()).isEqualTo(2);
        assertThat(result.getMaxScore()).isEqualTo(2);
        assertThat(result.isPassed()).isTrue();
    }

    @Test
    void takeQuiz_shouldFailWhenBelowSixtyPercent() {
        Quiz quiz = sampleQuiz();
        when(userRepository.findById(1L)).thenReturn(Optional.of(
                User.builder().id(1L).role(Role.STUDENT).build()));
        when(quizRepository.findWithQuestionsById(100L)).thenReturn(Optional.of(quiz));
        when(quizSubmissionRepository.save(any(QuizSubmission.class))).thenAnswer(inv -> inv.getArgument(0));

        QuizSubmission result = quizService.takeQuiz(1L, 100L,
                Map.of(1L, List.of(11L), 2L, List.of(22L)));

        assertThat(result.getScore()).isEqualTo(1);
        assertThat(result.getMaxScore()).isEqualTo(2);
        assertThat(result.isPassed()).isFalse();
    }

    @Test
    void takeQuiz_shouldThrowWhenStudentMissing() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizService.takeQuiz(404L, 100L, Map.of()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User (student) with id=404");
    }

    @Test
    void getById_shouldThrowWhenMissing() {
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}