package com.yoosang.oauth2_practice.user.exception;

import com.yoosang.oauth2_practice.common.exception.BaseException;

public class EmailVerificationException extends BaseException {

    private EmailVerificationException(EmailVerificationExceptionCode code, String message) {
        super(code.getHttpStatus(), code.getErrorCode(), message);
    }

    public static EmailVerificationException notFound(String token) {
        return new EmailVerificationException(EmailVerificationExceptionCode.TOKEN_NOT_FOUND,
                String.format("인증 토큰(%s)을 찾을 수 없습니다.", token));
    }

    public static EmailVerificationException expired(String token) {
        return new EmailVerificationException(EmailVerificationExceptionCode.TOKEN_EXPIRED,
                String.format("인증 토큰(%s)이 만료되었습니다.", token));
    }

    public static EmailVerificationException alreadyUsed(String token) {
        return new EmailVerificationException(EmailVerificationExceptionCode.TOKEN_ALREADY_USED,
                String.format("인증 토큰(%s)은 이미 사용되었습니다.", token));
    }
}
