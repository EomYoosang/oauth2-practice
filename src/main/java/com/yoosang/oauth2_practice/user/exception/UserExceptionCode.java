package com.yoosang.oauth2_practice.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserExceptionCode {
    USER_NOT_FOUND("USR001", "존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("USR002", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}
