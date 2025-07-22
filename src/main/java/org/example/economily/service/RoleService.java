package org.example.economily.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.RoleDto;
import org.example.economily.entity.UserRole;
import org.example.economily.mapper.RoleMapper;
import org.example.economily.repository.UserRepository;
import org.example.economily.repository.UserRoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final UserRoleRepository roleRepository;
    private final RoleMapper roleMapper;
    public ApiResponse create(RoleDto.CreateRole dto) {
        UserRole role = this.roleMapper.toEntity(dto);
        role.setCreatedAt(LocalDateTime.now());
        this.roleRepository.save(role);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("ROLE CREATED")
                .build();
    }

    public ApiResponse getAll() {
        List<UserRole> list = this.roleRepository.findAll();
        if (!list.isEmpty()) {
            return ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .data(this.roleMapper.dtoList(list))
                    .message("SUCCESS")
                    .build();
        }
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("SUCCESS")
                .data(new ArrayList<>())
                .build();
    }
}
