package org.example.economily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.economily.dto.ApiResponse;
import org.example.economily.entity.User;
import org.example.economily.service.UserService;
import org.example.economily.util.CurrentUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;
    @Operation(
            summary = "Get paginated list of all users",
            description = "Retrieves a paginated list of all users in the system. Accessible by admins or authorized users."
    )
    @GetMapping("/list")
    public ApiResponse getAll(
            @Parameter(description = "Page number for pagination") @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Page size for pagination") @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        return this.userService.getAll(PageRequest.of(page, size));
    }

    @Operation(
            summary = "Get information about the currently authenticated user",
            description = "Returns detailed information about the currently logged-in user based on the JWT token."
    )
    @GetMapping("/me")
    public ApiResponse getMe(
            @CurrentUser User user
    ) {
        return this.userService.getMe(user);
    }

    @Operation(
            summary = "Attach a user to a specific role (ADMIN only)",
            description = "Allows an administrator to assign a specific role to a user by their IDs. This action is restricted to ADMIN users only."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/attach-to-role")
    public ApiResponse attachToRole(
            @Parameter(description = "ID of the user to attach") @RequestParam Long userId,
            @Parameter(description = "ID of the role to attach to the user") @RequestParam Long roleId
    ) {
        return this.userService.attachToRole(userId, roleId);
    }

}
