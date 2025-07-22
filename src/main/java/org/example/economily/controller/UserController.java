package org.example.economily.controller;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.Current;
import org.example.economily.dto.ApiResponse;
import org.example.economily.entity.User;
import org.example.economily.service.RoleService;
import org.example.economily.service.UserService;
import org.example.economily.util.CurrentUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public ApiResponse getAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return this.userService.getAll(PageRequest.of(page,size));
    }

    @GetMapping("/me")
    public ApiResponse getMe(@CurrentUser User user) {
        return this.userService.getMe(user);
    }

    @PostMapping("/attach-to-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse attachToRole(@RequestParam Long userId,
                                    @RequestParam Long roleId) {
        return this.userService.attachToRole(userId,roleId);
    }
}
