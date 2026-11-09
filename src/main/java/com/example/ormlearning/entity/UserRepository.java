package com.example.ormlearning.repository;

import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    @EntityGraph(attributePaths = "profile")
    Optional<User> findWithProfileById(Long id);
}