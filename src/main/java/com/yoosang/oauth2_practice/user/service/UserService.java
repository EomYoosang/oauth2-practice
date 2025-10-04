package com.yoosang.oauth2_practice.user.service;

import com.yoosang.oauth2_practice.user.entity.EmailAccount;
import com.yoosang.oauth2_practice.user.entity.User;
import com.yoosang.oauth2_practice.user.exception.UserException;
import com.yoosang.oauth2_practice.user.repository.EmailAccountRepository;
import com.yoosang.oauth2_practice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailAccountRepository emailAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String email, String rawPassword) {
        if (emailAccountRepository.existsByEmail(email)) {
            throw UserException.duplicateEmail(email);
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = User.create(email);
        User savedUser = userRepository.save(user);
        EmailAccount emailAccount = EmailAccount.create(savedUser, email, encodedPassword);
        emailAccountRepository.save(emailAccount);
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> UserException.notFound(email));
    }
}
