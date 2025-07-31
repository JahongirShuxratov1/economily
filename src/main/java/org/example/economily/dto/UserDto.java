package org.example.economily.dto;

import lombok.*;
import org.example.economily.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
    @AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private UserStatus status;
    private LocalDateTime createdAt;
    private Set<String> roles;
}
