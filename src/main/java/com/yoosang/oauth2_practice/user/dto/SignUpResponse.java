package com.yoosang.oauth2_practice.user.dto;

import com.yoosang.oauth2_practice.user.entity.User;
import java.time.LocalDateTime;

public class SignUpResponse {
    private final Long id;
    private final String email;
    private final LocalDateTime createdAt;
    private final boolean emailVerified;
    private final String displayName;
    private final String role;

    private SignUpResponse(Long id, String email, LocalDateTime createdAt,
                           boolean emailVerified, String displayName, String role) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.emailVerified = emailVerified;
        this.displayName = displayName;
        this.role = role;
    }

    public static SignUpResponse from(User user) {
        return new SignUpResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt(),
                user.isEmailVerified(),
                user.getDisplayName(),
                user.getRole().name()
        );
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }
}
