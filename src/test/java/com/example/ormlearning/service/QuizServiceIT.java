package com.example.ormlearning.service;

import com.example.ormlearning.AbstractIntegrationTest;
import com.example.ormlearning.entity.AnswerOption;
import com.example.ormlearning.entity.Category;
import com.example.ormlearning.entity.Course;
import com.example.ormlearning.entity.Module;
import com.example.ormlearning.entity.Question;
import com.example.ormlearning.entity.QuestionType;
import com.example.ormlearning.entity.Quiz;
import com.example.ormlearning.entity.QuizSubmission;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.repository.AnswerOptionRepository;
import com.example.ormlearning.repository.CategoryRepository;
import com.example.ormlearning.repository.CourseRepository;
import com.example.ormlearning.repository.ModuleRepository;
import com.example.ormlearning.repository.QuestionRepository;
import com.example.ormlearning.repository.QuizRepository;
import com.example.ormlearning.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QuizServiceIT extends AbstractIntegrationTest {

    @Autowired private QuizService quizService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ModuleRepository moduleRepository;
    @Autowired private QuizRepository quizRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerOptionRepository answerOptionRepository;
    @Autowired private EntityManager entityManager;

    @Test
    void shouldTakeQuizAndCalculateScore() {
        User student = userRepository.save(User.builder()
                .name("QS").email("qs@example.com").role(Role.STUDENT).build());
        Category cat = categoryRepository.save(Category.builder().name("QCat").build());
        Course course = courseRepository.save(Course.builder().title("QC").category(cat).build());
        Module module = moduleRepository.save(Module.builder()
                .title("QM").orderIndex(1).course(course).build());

        Quiz quiz = quizRepository.save(Quiz.builder().title("Test Quiz").module(module).build());

        Question q1 = questionRepository.save(Question.builder()
                .text("2+2?").type(QuestionType.SINGLE_CHOICE)
                .orderIndex(1).points(1).quiz(quiz).build());
        AnswerOption correct = answerOptionRepository.save(AnswerOption.builder()
                .text("4").correct(true).orderIndex(1).question(q1).build());
        answerOptionRepository.save(AnswerOption.builder()
                .text("5").correct(false).orderIndex(2).question(q1).build());

        entityManager.flush();
        entityManager.clear();

        QuizSubmission result = quizService.takeQuiz(student.getId(), quiz.getId(),
                Map.of(q1.getId(), List.of(correct.getId())));

        assertThat(result.getScore()).isEqualTo(1);
        assertThat(result.getMaxScore()).isEqualTo(1);
        assertThat(result.isPassed()).isTrue();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void getWithQuestions_shouldEagerlyLoadQuestionsAndOptions() {
        User student = userRepository.save(User.builder()
                .name("QS2").email("qs2@example.com").role(Role.STUDENT).build());
        Category cat = categoryRepository.save(Category.builder().name("QCat2").build());
        Course course = courseRepository.save(Course.builder().title("QC2").category(cat).build());
        Module module = moduleRepository.save(Module.builder()
                .title("QM2").orderIndex(1).course(course).build());
        Quiz quiz = quizRepository.save(Quiz.builder().title("Test Quiz 2").module(module).build());
        Question q1 = questionRepository.save(Question.builder()
                .text("2+2?").type(QuestionType.SINGLE_CHOICE)
                .orderIndex(1).points(1).quiz(quiz).build());
        answerOptionRepository.save(AnswerOption.builder()
                .text("4").correct(true).orderIndex(1).question(q1).build());

        entityManager.flush();
        entityManager.clear();

        // No surrounding transaction — must NOT throw LazyInitializationException,
        // because @EntityGraph eagerly loads questions and questions.options.
        Quiz loaded = quizService.getWithQuestions(quiz.getId());
        assertThat(loaded.getQuestions()).hasSize(1);
        assertThat(loaded.getQuestions().get(0).getOptions()).hasSize(1);
    }
}