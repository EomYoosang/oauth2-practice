package com.yoosang.oauth2_practice.user.repository;

import com.yoosang.oauth2_practice.user.entity.EmailAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAccountRepository extends JpaRepository<EmailAccount, Long> {

    boolean existsByEmail(String email);

    Optional<EmailAccount> findByEmail(String email);
}
