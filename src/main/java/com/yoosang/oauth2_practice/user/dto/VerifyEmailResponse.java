package com.yoosang.oauth2_practice.user.dto;

import java.time.LocalDateTime;

public class VerifyEmailResponse {

    private final String email;
    private final boolean verified;
    private final LocalDateTime verifiedAt;

    private VerifyEmailResponse(String email, boolean verified, LocalDateTime verifiedAt) {
        this.email = email;
        this.verified = verified;
        this.verifiedAt = verifiedAt;
    }

    public static VerifyEmailResponse of(String email, boolean verified, LocalDateTime verifiedAt) {
        return new VerifyEmailResponse(email, verified, verifiedAt);
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerified() {
        return verified;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }
}
