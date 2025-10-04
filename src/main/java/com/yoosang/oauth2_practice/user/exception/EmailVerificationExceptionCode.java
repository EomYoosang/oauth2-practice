package com.yoosang.oauth2_practice.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmailVerificationExceptionCode {
    TOKEN_NOT_FOUND("VER001", "유효하지 않은 인증 토큰입니다.", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED("VER002", "인증 토큰이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    TOKEN_ALREADY_USED("VER003", "이미 사용된 인증 토큰입니다.", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}
