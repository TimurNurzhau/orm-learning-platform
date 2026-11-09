package com.example.ormlearning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions", indexes = {
        @Index(name = "idx_questions_quiz", columnList = "quiz_id"),
        @Index(name = "idx_questions_order", columnList = "quiz_id, order_index")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"quiz", "options"})
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionType type;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column
    private Integer points;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_questions_quiz"))
    private Quiz quiz;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<AnswerOption> options = new ArrayList<>();
}