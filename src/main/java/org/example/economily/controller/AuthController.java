package org.example.economily.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.auths.*;
import org.example.economily.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login-by-email")
    public ApiResponse loginByEmail(@Valid @RequestBody LoginDto dto) {
        return this.authService.loginByEmail(dto);
    }

    @PostMapping("/register-by-email")
    public ApiResponse registerByEmail(@Valid @RequestBody RegisterDto dto) {
        return this.authService.registerByEmail(dto);
    }

    @PostMapping("/verify")
    public ApiResponse verify(@RequestBody VerifyDto dto) {
        return this.authService.verify(dto);
    }

    @PostMapping("/forget-password")
    public ApiResponse forgetPassword(@RequestBody ForgetPasswordDto dto) {
        return this.authService.forgetPassword(dto);
    }

    @PostMapping("/check-password")
    public ApiResponse checkPassword(@RequestBody CheckForgetPasswordDto dto) {
        return this.authService.checkPassword(dto);
    }

    @PostMapping("/google-sign")
    public ApiResponse googleSign(@RequestParam String idToken) {
        return this.authService.googleSign(idToken);
    }

}
