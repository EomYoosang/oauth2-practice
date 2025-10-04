package com.yoosang.oauth2_practice.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_account_id", nullable = false)
    private EmailAccount emailAccount;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime confirmedAt;

    private EmailVerificationToken(EmailAccount emailAccount, LocalDateTime expiresAt, String token) {
        this.emailAccount = emailAccount;
        this.expiresAt = expiresAt;
        this.token = token;
        this.used = false;
    }

    public static EmailVerificationToken create(EmailAccount emailAccount, LocalDateTime expiresAt) {
        String tokenValue = UUID.randomUUID().toString().replaceAll("-", "");
        return new EmailVerificationToken(emailAccount, expiresAt, tokenValue);
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }

    public void markUsed(LocalDateTime confirmationTime) {
        this.used = true;
        this.confirmedAt = confirmationTime;
    }
}
