package com.example.ormlearning.repository;

import com.example.ormlearning.AbstractIntegrationTest;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateAndFindUserByEmail() {
        User user = User.builder()
                .name("Alice")
                .email("alice@example.com")
                .role(Role.STUDENT)
                .build();
        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findByEmail("alice@example.com"))
                .isPresent()
                .get()
                .extracting(User::getName)
                .isEqualTo("Alice");
    }

    @Test
    void shouldUpdateUser() {
        User user = userRepository.save(User.builder()
                .name("Bob")
                .email("bob@example.com")
                .role(Role.STUDENT)
                .build());

        user.setName("Bobby");
        userRepository.save(user);

        assertThat(userRepository.findById(user.getId()))
                .get()
                .extracting(User::getName)
                .isEqualTo("Bobby");
    }

    @Test
    void shouldDeleteUser() {
        User user = userRepository.save(User.builder()
                .name("Carol")
                .email("carol@example.com")
                .role(Role.TEACHER)
                .build());

        userRepository.delete(user);

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}