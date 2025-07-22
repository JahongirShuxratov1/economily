package org.example.economily.dto.auths;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgetPasswordDto {
    @NotBlank(message = "email cannot be null or empty")
    private String email;
    @NotBlank(message = "new password cannot be null or empty")
    private String newPassword;
}
