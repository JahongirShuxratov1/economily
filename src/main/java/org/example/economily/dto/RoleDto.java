package org.example.economily.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRole {
        private String name;
    }
}
