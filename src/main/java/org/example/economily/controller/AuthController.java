package org.example.economily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.auths.*;
import org.example.economily.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Login by email",
            description = "Authenticates the user using email and password."
    )
    @PostMapping("/login-by-email")
    public ApiResponse loginByEmail(@Valid @RequestBody LoginDto dto) {
        return this.authService.loginByEmail(dto);
    }

    @Operation(
            summary = "Register by email",
            description = "Registers a new user using email and password."
    )
    @PostMapping("/register-by-email")
    public ApiResponse registerByEmail(@Valid @RequestBody RegisterDto dto) {
        return this.authService.registerByEmail(dto);
    }

    @Operation(
            summary = "Verify code",
            description = "Verifies the code sent to the user's email during registration."
    )
    @PostMapping("/verify")
    public ApiResponse verify(@RequestBody VerifyDto dto) {
        return this.authService.verify(dto);
    }

    @Operation(
            summary = "Forget password",
            description = "Sends a reset code to the user's email if they forgot their password."
    )
    @PostMapping("/forget-password")
    public ApiResponse forgetPassword(@RequestBody ForgetPasswordDto dto) {
        return this.authService.forgetPassword(dto);
    }

    @Operation(
            summary = "Check password reset code",
            description = "Verifies the code sent to the email and allows password reset."
    )
    @PostMapping("/check-password")
    public ApiResponse checkPassword(@RequestBody CheckForgetPasswordDto dto) {
        return this.authService.checkPassword(dto);
    }

    @Operation(
            summary = "Google Sign-In",
            description = "Authenticates the user using Google ID token."
    )
    @PostMapping("/google-sign")
    public ApiResponse googleSign(@RequestParam String idToken) {
        return this.authService.googleSign(idToken);
    }
}
