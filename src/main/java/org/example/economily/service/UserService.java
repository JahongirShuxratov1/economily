package org.example.economily.service;

import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.entity.User;
import org.example.economily.entity.UserRole;
import org.example.economily.enums.UserStatus;
import org.example.economily.exceptions.ErrorCodes;
import org.example.economily.exceptions.ErrorMessageException;
import org.example.economily.mapper.UserMapper;
import org.example.economily.repository.UserRepository;
import org.example.economily.repository.UserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final UserMapper userMapper;

    /**
     * Returns paginated list of users
     */
    public ApiResponse getAll(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("List of users")
                .data(userMapper.dtoList(userPage.getContent()))
                .elements(userPage.getTotalElements())
                .pages(userPage.getTotalPages())
                .build();
    }

    /**
     * Returns the current user's profile
     */
    public ApiResponse getMe(User user) {
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Users retrieved")
                .data(userMapper.toDto(user))
                .build();
    }

    /**
     * Assigns a role to a user
     */
    public ApiResponse attachToRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorMessageException("User not found", ErrorCodes.NotFound));

        UserRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ErrorMessageException("Role not found", ErrorCodes.NotFound));

        if (!user.getRole().contains(role)) {
            user.getRole().add(role);
            userRepository.save(user);
        }

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Role changed")
                .build();
    }

    public ApiResponse ban(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorMessageException("User not found", ErrorCodes.NotFound));

        user.setStatus(UserStatus.INACTIVE);

        userRepository.save(user);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("User banned")
                .build();
    }

    public ApiResponse unban(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorMessageException("User not found", ErrorCodes.NotFound));

        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("User activated")
                .build();
    }
}
