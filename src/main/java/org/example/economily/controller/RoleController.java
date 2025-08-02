package org.example.economily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Role", description = "Endpoints for managing user roles")
public class RoleController {

    private final RoleService roleService;


    @Operation(
            summary = "Create a new role",
            description = "Creates a new role using the provided role data. Only accessible to SUPER_ADMIN users."
    )
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/create")
    public ApiResponse create(@RequestBody @NonNull RoleDto.CreateRole dto) {
        return this.roleService.create(dto);
    }

    @Operation(
            summary = "Get all roles",
            description = "Retrieves the list of all roles available in the system."
    )
    @GetMapping("/list")
    public ApiResponse getAll() {
        return this.roleService.getAll();
    }
}
