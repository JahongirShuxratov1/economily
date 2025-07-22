package org.example.economily.mapper;


import org.example.economily.dto.UserDto;
import org.example.economily.dto.auths.RegisterDto;
import org.example.economily.entity.User;
import org.example.economily.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) return null;

        Set<String> roles = user.getRole() != null
                ? user.getRole().stream()
                .map(r -> r.getName().toUpperCase())
                .collect(Collectors.toSet())
                : Set.of();

        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullname())
                .email(user.getEmail())
                .roles(roles)
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public List<UserDto> dtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
