package com.yoosang.oauth2_practice.user.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoosang.oauth2_practice.config.SecurityConfig;
import com.yoosang.oauth2_practice.user.dto.SignUpRequest;
import com.yoosang.oauth2_practice.user.dto.VerifyEmailRequest;
import com.yoosang.oauth2_practice.user.entity.User;
import com.yoosang.oauth2_practice.user.exception.UserException;
import com.yoosang.oauth2_practice.user.service.EmailVerificationService;
import com.yoosang.oauth2_practice.user.service.EmailVerificationService.EmailVerificationResult;
import com.yoosang.oauth2_practice.user.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    @Test
    @DisplayName("회원가입 요청이 유효하면 201 Created를 반환한다")
    void signUp_shouldReturnCreated() throws Exception {
        // given
        String email = "signup@example.com";
        String rawPassword = "password123";

        User user = User.create(email);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());

        given(userService.registerUser(email, rawPassword)).willReturn(user);

        SignUpRequest request = new SignUpRequest(email, rawPassword);

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.emailVerified").value(false))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.displayName").value("signup"));
    }

    @Test
    @DisplayName("잘못된 입력이면 검증 오류 응답을 반환한다")
    void signUp_shouldReturnValidationError() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("invalid-email", "short");

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("중복 이메일이면 도메인 예외 응답을 반환한다")
    void signUp_shouldReturnConflictWhenEmailDuplicated() throws Exception {
        // given
        String email = "duplicate@example.com";
        SignUpRequest request = new SignUpRequest(email, "password123");
        given(userService.registerUser(anyString(), anyString()))
                .willThrow(UserException.duplicateEmail(email));

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USR002"))
                .andExpect(jsonPath("$.message").value("이메일 duplicate@example.com는 이미 사용 중입니다."));
    }

    @Test
    @DisplayName("인증 토큰이 유효하면 이메일 인증을 완료한다")
    void verifyEmail_shouldReturnOk() throws Exception {
        String token = "test-token";
        EmailVerificationResult result = EmailVerificationResult.of("tester1@example.com", true, LocalDateTime.now());
        given(emailVerificationService.verifyToken(token)).willReturn(result);

        VerifyEmailRequest request = new VerifyEmailRequest(token);

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("tester1@example.com"))
                .andExpect(jsonPath("$.verified").value(true));
    }
}
