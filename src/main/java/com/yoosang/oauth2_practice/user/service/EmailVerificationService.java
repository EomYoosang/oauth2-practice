package com.yoosang.oauth2_practice.user.service;

import com.yoosang.oauth2_practice.common.exception.BaseException;
import com.yoosang.oauth2_practice.config.properties.EmailVerificationProperties;
import com.yoosang.oauth2_practice.user.entity.EmailAccount;
import com.yoosang.oauth2_practice.user.entity.EmailVerificationToken;
import com.yoosang.oauth2_practice.user.entity.User;
import com.yoosang.oauth2_practice.user.exception.EmailVerificationException;
import com.yoosang.oauth2_practice.user.exception.EmailVerificationExceptionCode;
import com.yoosang.oauth2_practice.user.repository.EmailVerificationTokenRepository;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailVerificationProperties properties;
    private final JavaMailSender mailSender;
    private final Clock clock = Clock.systemDefaultZone();

    @Transactional
    public void sendVerificationEmail(@NonNull EmailAccount emailAccount) {
        EmailVerificationToken token = EmailVerificationToken.create(
                emailAccount,
                LocalDateTime.now(clock).plusMinutes(properties.getExpirationMinutes())
        );
        emailVerificationTokenRepository.save(token);
        mailSender.send(buildMessage(emailAccount, token));
    }

    @Transactional
    public EmailVerificationResult verifyToken(@NonNull String tokenValue) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> EmailVerificationException.notFound(tokenValue));

        if (token.isExpired(LocalDateTime.now(clock))) {
            throw EmailVerificationException.expired(tokenValue);
        }

        if (token.isUsed()) {
            throw EmailVerificationException.alreadyUsed(tokenValue);
        }

        EmailAccount emailAccount = token.getEmailAccount();
        User user = emailAccount.getUser();
        emailAccount.markVerified();
        user.verifyEmail();
        token.markUsed(LocalDateTime.now(clock));
        return EmailVerificationResult.of(emailAccount.getEmail(), user.isEmailVerified(), token.getConfirmedAt());
    }

    private SimpleMailMessage buildMessage(EmailAccount emailAccount, EmailVerificationToken token) {
        SimpleMailMessage message = new SimpleMailMessage();
        String verificationLink = properties.getBaseUrl() + token.getToken();
        message.setTo(emailAccount.getEmail());
        message.setSubject("[OAuth2 Practice] 이메일 인증을 완료해주세요");
        message.setText("안녕하세요,\n\n아래 링크를 클릭하여 이메일 인증을 완료해주세요.\n" + verificationLink
                + "\n\n본 메일은 "+ properties.getFromName() +"에서 발송되었습니다.");
        return message;
    }

    public record EmailVerificationResult(String email, boolean verified, LocalDateTime verifiedAt) {
        public static EmailVerificationResult of(String email, boolean verified, LocalDateTime verifiedAt) {
            return new EmailVerificationResult(email, verified, verifiedAt);
        }
    }
}
