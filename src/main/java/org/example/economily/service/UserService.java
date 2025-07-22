package org.example.economily.service;

import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.entity.User;
import org.example.economily.entity.UserRole;
import org.example.economily.exceptions.ErrorCodes;
import org.example.economily.exceptions.ErrorMessageException;
import org.example.economily.mapper.UserMapper;
import org.example.economily.repository.UserRepository;
import org.example.economily.repository.UserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final UserMapper userMapper;

    public ApiResponse getAll(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Foydalanuvchilar ro‘yxati")
                .data(userMapper.dtoList(userPage.getContent()))
                .elements(userPage.getTotalElements())
                .pages(userPage.getTotalPages())
                .build();
    }

    public ApiResponse getMe(User user) {
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Foydalanuvchi qabul qilindi")
                .data(userMapper.toDto(user))
                .build();
    }

    public ApiResponse attachToRole(Long userId, Long roleId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ErrorMessageException("User not found", ErrorCodes.NotFound));
        UserRole role =this.roleRepository.findById(roleId)
                .orElseThrow(() -> new ErrorMessageException("User not found", ErrorCodes.NotFound));
        user.getRole().add(role);
        userRepository.save(user);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Foydalanuvchi roli o'zgartirildi")
                .build();
    }
}
