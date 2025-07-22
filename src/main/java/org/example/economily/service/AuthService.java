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
        // 1. Tekshirish: email va parol bo‘sh emasmi
        if (dto.getEmail() == null || dto.getEmail().isBlank() ||
                dto.getPassword() == null || dto.getPassword().isBlank()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Email yoki parol kiritilmagan")
                    .build();
        }

        // 2. User bazada bormi?
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isEmpty()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Login yoki parol noto‘g‘ri")
                    .build();
        }

        User user = optionalUser.get();

        // 3. Parol to‘g‘riligini tekshirish
        try {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), dto.getPassword());
            authenticationManager.authenticate(authentication); // Bu yerda xatolik bo‘lsa exception otadi
        } catch (Exception e) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Login yoki parol noto‘g‘ri")
                    .build();
        }

        // 4. JWT token yaratish
        String jwtToken = jwtUtil.generateToken(user.getUsername());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Login muvaffaqiyatli")
                .data(jwtToken)
                .build();
    }


    @Transactional
    public ApiResponse registerByEmail(RegisterDto dto) {
        User exist = userRepository.findByEmail(dto.getEmail()).orElse(null);
        // 1. Email mavjudmi?
        if (exist!=null) {
            if (exist.getStatus() == UserStatus.PENDING){
                this.userRepository.delete(exist);
                userRepository.flush();
            }else {
                return ApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Bu email bilan foydalanuvchi allaqachon mavjud")
                        .build();
            }
        }

        // 2. Email formatini tekshirish
        if (!utils.checkEmail(dto.getEmail())) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Email formati noto‘g‘ri")
                    .build();
        }

        // 3. Kod generatsiya va yuborish
        String code = utils.getCode();
        boolean sent = utils.sendCodeToMail(dto.getEmail(), code);
        if (!sent) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Kod yuborishda xatolik yuz berdi")
                    .build();
        }

        // 4. Foydalanuvchini yaratish
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullname(dto.getFullName());
        user.setStatus(UserStatus.PENDING);
        user.setCreatedAt(LocalDateTime.now());
        // Set default role
        UserRole role = roleRepository.findByName("USER")
                .orElseThrow(() -> new ErrorMessageException("USER roli topilmadi", ErrorCodes.NotFound));
        user.setRole(Set.of(role));
        user = userRepository.save(user);

        // 5. Kodni saqlash (hech qanday device yo‘q!)
        Code codeEntity = new Code();
        codeEntity.setUser(user);
        codeEntity.setCode(code);
        codeEntity.setCreatedAt(LocalDateTime.now());
        codeRepository.save(codeEntity);

        // 7. Javob qaytarish
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Tasdiqlash kodi emailga yuborildi")
                .build();
    }


    public ApiResponse verify(VerifyDto dto) {
        // 1. User mavjudligini tekshirish
        User user = userRepository.findByEmailPending(dto.getEmail())
                .orElseThrow(() -> new ErrorMessageException("Foydalanuvchi topilmadi", ErrorCodes.NotFound));

        // 2. Email va code orqali Code obyektini olish
        Code code = codeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ErrorMessageException("Kod topilmadi", ErrorCodes.NotFound));

        // 3. Email tekshirish
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Email kiritilmagan")
                    .build();
        }

        if (!code.getCode().equals(dto.getCode())) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Kod noto‘g‘ri")
                    .build();
        }

        // 4. Kod to‘g‘ri - userni faollashtiramiz
        user.setStatus(UserStatus.ACTIVE);
        user.setEmail(dto.getEmail());
        userRepository.save(user);

        code.setApprovedAt(LocalDateTime.now());
        codeRepository.save(code);

        // 5. Token yaratish yoki mavjud bo‘lsa update qilish
        String jwt = jwtUtil.generateToken(user.getUsername());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Foydalanuvchi muvaffaqiyatli faollashtirildi")
                .data(jwt)
                .build();
    }

    public ApiResponse forgetPassword(ForgetPasswordDto dto) {
        // 1. Find user by email
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ErrorMessageException("Foydalanuvchi topilmadi", ErrorCodes.NotFound));

        // 2. Generate and send verification code
        String code = utils.getCode();
        boolean sent = utils.sendCodeToMail(dto.getEmail(), code);
        if (!sent) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Kod yuborishda xatolik yuz berdi")
                    .build();
        }

        // 3. Save code to DB
        Code codeEntity = new Code();
        codeEntity.setUser(user);
        codeEntity.setCode(code);
        codeEntity.setCreatedAt(LocalDateTime.now());
        codeRepository.save(codeEntity);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Tasdiqlash kodi yuborildi")
                .build();
    }


    public ApiResponse checkPassword(CheckForgetPasswordDto dto) {
        // 1. Find user by email
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ErrorMessageException("Foydalanuvchi topilmadi", ErrorCodes.NotFound));

        // 2. Get code from DB
        Code code = codeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ErrorMessageException("Kod topilmadi", ErrorCodes.NotFound));

        // 3. Check email
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Email kiritilmagan")
                    .build();
        }

        // 4. Check code correctness
        if (!code.getCode().equals(dto.getCode())) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Kod noto‘g‘ri")
                    .build();
        }

        // 5. Update password
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // 6. Mark code as used
        code.setApprovedAt(LocalDateTime.now());
        codeRepository.save(code);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Parol muvaffaqiyatli o‘zgartirildi")
                .build();
    }

    public ApiResponse googleSign(String idToken) {
        try {
            // 1. Verify Firebase token
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String email = firebaseToken.getEmail();
            String fullName = firebaseToken.getName();

            // 2. Validate email format
            if (!utils.checkEmail(email)) {
                return ApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Email formati noto‘g‘ri")
                        .build();
            }

            // 3. Check if user exists
            User user = userRepository.findByEmail(email).orElse(null);
            String jwtToken;

            if (user != null) {
                // 4. User exists — generate token and return
                jwtToken = jwtUtil.generateToken(user.getUsername());

            } else {
                // 5. User doesn't exist — create one
                user = new User();
                user.setEmail(email);
                user.setFullname(fullName);
                user.setStatus(UserStatus.ACTIVE);
                user.setCreatedAt(LocalDateTime.now());

                // Set default role
                UserRole role = roleRepository.findByName("USER")
                        .orElseThrow(() -> new ErrorMessageException("USER roli topilmadi", ErrorCodes.NotFound));
                user.setRole(Set.of(role));

                user = userRepository.save(user);

                jwtToken = jwtUtil.generateToken(user.getUsername());

            }

            return ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .message("Google orqali login muvaffaqiyatli")
                    .data(jwtToken)
                    .build();

        } catch (FirebaseAuthException e) {
            return ApiResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("Google token noto‘g‘ri yoki eskirgan: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Xatolik yuz berdi: " + e.getMessage())
                    .build();
        }
    }

}
