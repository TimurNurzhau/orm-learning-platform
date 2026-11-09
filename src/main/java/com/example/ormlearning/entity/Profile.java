package com.example.ormlearning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "user")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Size(max = 2000)
    @Column(length = 2000)
    private String bio;

    @Size(max = 500)
    @Column(length = 500)
    private String avatarUrl;

    @Size(max = 255)
    @Column(length = 255)
    private String phone;

    @Size(max = 255)
    @Column(length = 255)
    private String location;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_profiles_user"))
    private User user;
}
