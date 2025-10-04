package com.yoosang.oauth2_practice.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(Environment environment) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(environment.getProperty("MAIL_HOST", "smtp.gmail.com"));
        sender.setPort(Integer.parseInt(environment.getProperty("MAIL_PORT", "587")));
        sender.setUsername(environment.getProperty("MAIL_USERNAME"));
        sender.setPassword(environment.getProperty("MAIL_PASSWORD"));

        Properties properties = sender.getJavaMailProperties();
        properties.put("mail.smtp.auth", environment.getProperty("MAIL_SMTP_AUTH", "true"));
        properties.put("mail.smtp.starttls.enable", environment.getProperty("MAIL_SMTP_STARTTLS_ENABLE", "true"));
        return sender;
    }
}
