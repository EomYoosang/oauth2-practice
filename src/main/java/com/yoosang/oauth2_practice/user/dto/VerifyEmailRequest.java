package com.yoosang.oauth2_practice.user.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyEmailRequest {

    @NotBlank(message = "인증 토큰은 필수 값입니다.")
    private String token;

    public VerifyEmailRequest() {
    }

    public VerifyEmailRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
