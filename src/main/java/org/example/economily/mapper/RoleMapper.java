package org.example.economily.mapper;

import org.example.economily.dto.RoleDto;
import org.example.economily.entity.UserRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoleMapper {
    public UserRole toEntity(RoleDto.CreateRole dto) {
        return UserRole.builder()
                .name(dto.getName())
                .build();
    }

    public RoleDto toDto(UserRole role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .createdAt(role.getCreatedAt())
                .build();
    }

    public List<RoleDto> dtoList(List<UserRole> roleList) {
        if (roleList != null && !roleList.isEmpty()) {
            return roleList.stream().map(this::toDto).toList();
        }
        return new ArrayList<>();
    }
}
