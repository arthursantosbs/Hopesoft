package com.hopesoft.controller;

import com.hopesoft.dto.AuthenticatedUserResponse;
import com.hopesoft.dto.LoginRequest;
import com.hopesoft.dto.LoginResponse;
import com.hopesoft.security.HopesoftUserDetails;
import com.hopesoft.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserResponse> me(
            @AuthenticationPrincipal HopesoftUserDetails userDetails
    ) {
        return ResponseEntity.ok(userDetails.toResponse());
    }
}
