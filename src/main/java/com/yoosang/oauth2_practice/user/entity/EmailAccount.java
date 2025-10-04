package com.yoosang.oauth2_practice.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "email_accounts")
public class EmailAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false)
    private boolean primaryAccount;

    private EmailAccount(User user, String email, String password, boolean primaryAccount) {
        this.user = user;
        this.email = email;
        this.password = password;
        this.primaryAccount = primaryAccount;
    }

    public static EmailAccount create(User user, String email, String encodedPassword) {
        return new EmailAccount(user, email, encodedPassword, true);
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
