package shelfsync.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminLoginRequestDto(
        @NotBlank(message = "Email address is required")
        @Email(message = "Please provide a valid email address")
        String adminEmail,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
        String password
) {}
