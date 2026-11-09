package com.example.ormlearning.web;

import com.example.ormlearning.dto.NotificationRequest;
import com.example.ormlearning.dto.NotificationResponse;
import com.example.ormlearning.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "User notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Send a notification")
    public ResponseEntity<NotificationResponse> send(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NotificationResponse.from(
                        notificationService.send(request.userId(), request.title(), request.message())));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "List all notifications for a user")
    public List<NotificationResponse> list(@PathVariable Long userId) {
        return notificationService.getByUser(userId).stream().map(NotificationResponse::from).toList();
    }

    @GetMapping("/users/{userId}/unread")
    @Operation(summary = "List unread notifications for a user")
    public List<NotificationResponse> unread(@PathVariable Long userId) {
        return notificationService.getUnreadByUser(userId).stream().map(NotificationResponse::from).toList();
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.noContent().build();
    }
}