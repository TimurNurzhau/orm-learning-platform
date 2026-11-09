package com.example.ormlearning.service;

import com.example.ormlearning.entity.Profile;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.exception.DuplicateResourceException;
import com.example.ormlearning.exception.ResourceNotFoundException;
import com.example.ormlearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User with email=" + user.getEmail() + " already exists");
        }
        return userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email=" + email + " not found"));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Transactional
    public User update(Long id, User updated) {
        User existing = getById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setRole(updated.getRole());
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    @Transactional
    public Profile upsertProfile(Long userId, Profile profile) {
        User user = getById(userId);
        Profile existing = user.getProfile();
        if (existing == null) {
            profile.setUser(user);
            user.setProfile(profile);
            return profile;
        }
        existing.setBio(profile.getBio());
        existing.setAvatarUrl(profile.getAvatarUrl());
        existing.setPhone(profile.getPhone());
        existing.setLocation(profile.getLocation());
        return existing;
    }
}