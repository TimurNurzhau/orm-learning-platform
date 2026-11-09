package com.example.ormlearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_submissions", indexes = {
        @Index(name = "idx_quiz_submissions_quiz", columnList = "quiz_id"),
        @Index(name = "idx_quiz_submissions_student", columnList = "student_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"quiz", "student"})
public class QuizSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    @Column(name = "passed", nullable = false)
    private boolean passed;

    @Column(name = "taken_at", nullable = false)
    private LocalDateTime takenAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_quiz_submissions_quiz"))
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_quiz_submissions_student"))
    private User student;

    @PrePersist
    void onCreate() {
        if (takenAt == null) takenAt = LocalDateTime.now();
    }
}