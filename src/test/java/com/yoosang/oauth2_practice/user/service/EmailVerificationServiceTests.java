package com.yoosang.oauth2_practice.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import com.yoosang.oauth2_practice.config.properties.EmailVerificationProperties;
import com.yoosang.oauth2_practice.user.entity.EmailAccount;
import com.yoosang.oauth2_practice.user.entity.EmailVerificationToken;
import com.yoosang.oauth2_practice.user.entity.User;
import com.yoosang.oauth2_practice.user.exception.EmailVerificationException;
import com.yoosang.oauth2_practice.user.repository.EmailAccountRepository;
import com.yoosang.oauth2_practice.user.repository.EmailVerificationTokenRepository;
import com.yoosang.oauth2_practice.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@Import(EmailVerificationService.class)
@EnableConfigurationProperties(EmailVerificationProperties.class)
@TestPropertySource(properties = {
        "app.auth.verification.base-url=http://localhost/verify?token=",
        "app.auth.verification.expiration-minutes=60",
        "app.auth.verification.from-name=OAuth2 Practice"
})
class EmailVerificationServiceTests {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailAccountRepository emailAccountRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("이메일 인증 메일을 발송하면 토큰이 생성된다")
    void sendVerificationEmail_shouldCreateToken() {
        EmailAccount emailAccount = prepareEmailAccount();

        emailVerificationService.sendVerificationEmail(emailAccount);

        assertThat(tokenRepository.findAll()).hasSize(1);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(captor.capture());
        assertThat(captor.getValue().getTo()).containsExactly(emailAccount.getEmail());
    }

    @Test
    @DisplayName("토큰 검증에 성공하면 사용자 이메일이 검증 처리된다")
    void verifyToken_shouldMarkVerified() {
        EmailAccount emailAccount = prepareEmailAccount();
        EmailVerificationToken token = tokenRepository.save(EmailVerificationToken.create(
                emailAccount,
                LocalDateTime.now().plusMinutes(60)
        ));

        EmailVerificationService.EmailVerificationResult result = emailVerificationService.verifyToken(token.getToken());

        assertThat(result.email()).isEqualTo(emailAccount.getEmail());
        assertThat(result.verified()).isTrue();
        assertThat(emailAccount.isVerified()).isTrue();
        assertThat(emailAccount.getUser().isEmailVerified()).isTrue();
        EmailVerificationToken refreshed = tokenRepository.findById(token.getId()).orElseThrow();
        assertThat(refreshed.isUsed()).isTrue();
        assertThat(refreshed.getConfirmedAt()).isNotNull();
    }

    @Test
    @DisplayName("만료된 토큰은 예외를 발생시킨다")
    void verifyToken_expiredToken() {
        EmailAccount emailAccount = prepareEmailAccount();
        EmailVerificationToken token = tokenRepository.save(EmailVerificationToken.create(
                emailAccount,
                LocalDateTime.now().minusMinutes(1)
        ));

        assertThatThrownBy(() -> emailVerificationService.verifyToken(token.getToken()))
                .isInstanceOf(EmailVerificationException.class)
                .hasMessageContaining("만료");
    }

    private EmailAccount prepareEmailAccount() {
        User user = userRepository.save(User.create("tester@example.com"));
        EmailAccount emailAccount = EmailAccount.create(user, "tester@example.com", "encoded-password");
        return emailAccountRepository.save(emailAccount);
    }
}
