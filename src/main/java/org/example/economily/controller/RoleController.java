package org.example.economily.controller;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.dto.RoleDto;
import org.example.economily.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class RoleController {
    private final RoleService roleService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/create")
    public ApiResponse create(@RequestBody @NonNull RoleDto.CreateRole dto) {
        return this.roleService.create(dto);
    }

    @GetMapping("/list")
    public ApiResponse getAll() {
        return this.roleService.getAll();
    }
}
