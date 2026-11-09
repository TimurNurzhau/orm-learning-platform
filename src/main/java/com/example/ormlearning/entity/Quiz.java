package com.example.ormlearning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes", indexes = {
        @Index(name = "idx_quizzes_module", columnList = "module_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"module", "questions", "submissions"})
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_quizzes_module"))
    private Module module;

    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuizSubmission> submissions = new ArrayList<>();
}