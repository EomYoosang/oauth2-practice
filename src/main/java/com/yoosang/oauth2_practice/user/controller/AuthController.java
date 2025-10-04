package com.yoosang.oauth2_practice.user.controller;

import com.yoosang.oauth2_practice.user.dto.SignUpRequest;
import com.yoosang.oauth2_practice.user.dto.SignUpResponse;
import com.yoosang.oauth2_practice.user.entity.User;
import com.yoosang.oauth2_practice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        User createdUser = userService.registerUser(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(SignUpResponse.from(createdUser));
    }
}
