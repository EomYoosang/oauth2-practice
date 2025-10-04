package com.yoosang.oauth2_practice;

import com.yoosang.oauth2_practice.config.properties.EmailVerificationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EmailVerificationProperties.class)
public class Oauth2PracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2PracticeApplication.class, args);
	}

}
