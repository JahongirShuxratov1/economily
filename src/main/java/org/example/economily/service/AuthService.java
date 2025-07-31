package org.example.economily.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.auths.*;
import org.example.economily.entity.Code;
import org.example.economily.entity.User;
import org.example.economily.entity.UserRole;
import org.example.economily.enums.UserStatus;
import org.example.economily.exceptions.ErrorCodes;
import org.example.economily.exceptions.ErrorMessageException;
import org.example.economily.repository.CodeRepository;
import org.example.economily.repository.UserRepository;
import org.example.economily.repository.UserRoleRepository;
import org.example.economily.util.JwtUtil;
import org.example.economily.util.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final Utils utils;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository roleRepository;

    public ApiResponse loginByEmail(LoginDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank() ||
                dto.getPassword() == null || dto.getPassword().isBlank()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Email or password is missing")
                    .build();
        }

        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isEmpty()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Incorrect email or password")
                    .build();
        }

        User user = optionalUser.get();

        try {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), dto.getPassword());
            authenticationManager.authenticate(authentication);
        } catch (Exception e) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Incorrect email or password")
                    .build();
        }

        String jwtToken = jwtUtil.generateToken(user.getUsername());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Login successful")
                .data(jwtToken)
                .build();
    }

    @Transactional
    public ApiResponse registerByEmail(RegisterDto dto) {
        User exist = userRepository.findByEmail(dto.getEmail()).orElse(null);
        if (exist != null) {
            if (exist.getStatus() == UserStatus.PENDING) {
                userRepository.delete(exist);
                userRepository.flush();
            } else {
                return ApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("User already exists with this email")
                        .build();
            }
        }

        if (!utils.checkEmail(dto.getEmail())) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid email format")
                    .build();
        }

        String code = utils.getCode();
        boolean sent = utils.sendCodeToMail(dto.getEmail(), code);
        if (!sent) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Failed to send verification code")
                    .build();
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullname(dto.getFullName());
        user.setStatus(UserStatus.PENDING);
        user.setCreatedAt(LocalDateTime.now());

        UserRole role = roleRepository.findByName("USER")
                .orElseThrow(() -> new ErrorMessageException("USER role not found", ErrorCodes.NotFound));
        user.setRole(Set.of(role));
        user = userRepository.save(user);

        Code codeEntity = new Code();
        codeEntity.setUser(user);
        codeEntity.setCode(code);
        codeEntity.setCreatedAt(LocalDateTime.now());
        codeRepository.save(codeEntity);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Verification code sent to email")
                .build();
    }

    public ApiResponse verify(VerifyDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ErrorMessageException("User not found", ErrorCodes.NotFound));

        Code code = codeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ErrorMessageException("Code not found", ErrorCodes.NotFound));

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Email is missing")
                    .build();
        }

        if (!code.getCode().equals(dto.getCode())) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid code")
                    .build();
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setEmail(dto.getEmail());
        userRepository.save(user);

        code.setApprovedAt(LocalDateTime.now());
        codeRepository.save(code);

        String jwt = jwtUtil.generateToken(user.getUsername());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("User successfully verified")
                .data(jwt)
                .build();
    }

    public ApiResponse forgetPassword(ForgetPasswordDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ErrorMessageException("User not found", ErrorCodes.NotFound));

        String code = utils.getCode();
        boolean sent = utils.sendCodeToMail(dto.getEmail(), code);
        if (!sent) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Failed to send verification code")
                    .build();
        }

        Code codeEntity = new Code();
        codeEntity.setUser(user);
        codeEntity.setCode(code);
        codeEntity.setCreatedAt(LocalDateTime.now());
        codeRepository.save(codeEntity);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Verification code sent")
                .build();
    }

    public ApiResponse checkPassword(CheckForgetPasswordDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ErrorMessageException("User not found", ErrorCodes.NotFound));

        Code code = codeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ErrorMessageException("Code not found", ErrorCodes.NotFound));

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Email is missing")
                    .build();
        }

        if (!code.getCode().equals(dto.getCode())) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid verification code")
                    .build();
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        code.setApprovedAt(LocalDateTime.now());
        codeRepository.save(code);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Password successfully changed")
                .build();
    }

    public ApiResponse googleSign(String idToken) {
        try {
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String email = firebaseToken.getEmail();
            String fullName = firebaseToken.getName();

            if (!utils.checkEmail(email)) {
                return ApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Invalid email format")
                        .build();
            }

            User user = userRepository.findByEmail(email).orElse(null);
            String jwtToken;

            if (user != null) {
                jwtToken = jwtUtil.generateToken(user.getUsername());
            } else {
                user = new User();
                user.setEmail(email);
                user.setFullname(fullName);
                user.setStatus(UserStatus.ACTIVE);
                user.setCreatedAt(LocalDateTime.now());

                UserRole role = roleRepository.findByName("USER")
                        .orElseThrow(() -> new ErrorMessageException("USER role not found", ErrorCodes.NotFound));
                user.setRole(Set.of(role));

                user = userRepository.save(user);
                jwtToken = jwtUtil.generateToken(user.getUsername());
            }

            return ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .message("Login via Google successful")
                    .data(jwtToken)
                    .build();

        } catch (FirebaseAuthException e) {
            return ApiResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("Invalid or expired Google token: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("An error occurred: " + e.getMessage())
                    .build();
        }
    }
}
