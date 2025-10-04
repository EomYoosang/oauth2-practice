package com.yoosang.oauth2_practice.user.exception;

import com.yoosang.oauth2_practice.common.exception.BaseException;

public class UserException extends BaseException {

    private UserException(UserExceptionCode code, String message) {
        super(code.getHttpStatus(), code.getErrorCode(), message);
    }

    private UserException(UserExceptionCode code) {
        this(code, code.getMessage());
    }

    public static UserException notFound(String email) {
        return new UserException(UserExceptionCode.USER_NOT_FOUND,
                String.format("이메일 %s로 가입된 사용자를 찾을 수 없습니다.", email));
    }

    public static UserException duplicateEmail(String email) {
        return new UserException(UserExceptionCode.DUPLICATE_EMAIL,
                String.format("이메일 %s는 이미 사용 중입니다.", email));
    }
}
