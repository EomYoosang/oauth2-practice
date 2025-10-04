package com.yoosang.oauth2_practice.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yoosang.oauth2_practice.user.entity.EmailAccount;
import com.yoosang.oauth2_practice.user.entity.User;
import com.yoosang.oauth2_practice.user.entity.UserRole;
import com.yoosang.oauth2_practice.user.exception.UserException;
import com.yoosang.oauth2_practice.user.exception.UserExceptionCode;
import com.yoosang.oauth2_practice.user.repository.EmailAccountRepository;
import com.yoosang.oauth2_practice.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
@Import({UserService.class, UserServiceTests.TestPasswordEncoderConfig.class})
class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailAccountRepository emailAccountRepository;

    @Test
    @DisplayName("회원 가입 시 비밀번호를 암호화하고 저장한다")
    void registerUser_shouldEncodePasswordAndPersist() {
        // given
        String email = "test@example.com";
        String rawPassword = "password123";

        // when
        User savedUser = userService.registerUser(email, rawPassword);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        EmailAccount emailAccount = emailAccountRepository.findByEmail(email).orElseThrow();
        assertThat(passwordEncoder.matches(rawPassword, emailAccount.getPassword())).isTrue();
        assertThat(savedUser.isEmailVerified()).isFalse();
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("이미 가입된 이메일이면 예외를 발생시킨다")
    void registerUser_shouldThrowWhenEmailDuplicate() {
        // given
        String email = "duplicate@example.com";
        User existingUser = userRepository.save(User.create(email));
        emailAccountRepository.save(EmailAccount.create(existingUser, email, passwordEncoder.encode("password123")));

        // when & then
        assertThatThrownBy(() -> userService.registerUser(email, "anotherPassword"))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(email)
                .satisfies(exception -> assertThat(((UserException) exception).getErrorCode())
                        .isEqualTo(UserExceptionCode.DUPLICATE_EMAIL.getErrorCode()));
    }

    @TestConfiguration
    static class TestPasswordEncoderConfig {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
