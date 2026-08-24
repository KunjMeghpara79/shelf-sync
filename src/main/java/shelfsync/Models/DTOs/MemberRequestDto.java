package shelfsync.Models.DTOs;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberRequestDto(
        @NotBlank
        String memberName,

        @NotBlank
        @Email
        String memberEmail,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a digit, and a special character (@$!%*?&)"
        )
        String password
) {
}
