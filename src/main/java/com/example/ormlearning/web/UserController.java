package com.example.ormlearning.web;

import com.example.ormlearning.dto.ProfileRequest;
import com.example.ormlearning.dto.UserCreateRequest;
import com.example.ormlearning.dto.UserResponse;
import com.example.ormlearning.entity.Profile;
import com.example.ormlearning.entity.Role;
import com.example.ormlearning.entity.User;
import com.example.ormlearning.service.UserService;
import com.example.ormlearning.dto.EnrollmentResponse;
import com.example.ormlearning.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserService userService;
    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Create a user")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .role(request.role())
                .build();
        User saved = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(saved));
    }

    @GetMapping
    @Operation(summary = "List users, optional role filter")
    public List<UserResponse> list(@RequestParam(required = false) Role role) {
        List<User> users = (role != null) ? userService.getByRole(role) : userService.getAll();
        return users.stream().map(UserResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by id")
    public UserResponse get(@PathVariable Long id) {
        return UserResponse.from(userService.getById(id));
    }

    @PutMapping("/{id}/profile")
    @Operation(summary = "Upsert user profile")
    public ResponseEntity<Void> upsertProfile(@PathVariable Long id, @Valid @RequestBody ProfileRequest request) {
        Profile profile = Profile.builder()
                .bio(request.bio())
                .avatarUrl(request.avatarUrl())
                .phone(request.phone())
                .location(request.location())
                .build();
        userService.upsertProfile(id, profile);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/courses")
    @Operation(summary = "List courses a student is enrolled in")
    public List<EnrollmentResponse> courses(@PathVariable Long id) {
        return enrollmentService.getByStudent(id).stream().map(EnrollmentResponse::from).toList();
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
