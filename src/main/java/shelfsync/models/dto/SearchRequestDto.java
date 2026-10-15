package shelfsync.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SearchRequestDto(
        @NotBlank(message = "name cannot be blank.")
        @Pattern(regexp = "^(?!-?\\d+(\\.\\d+)?$).*$", message = "name cannot consist of numbers only.")
        String name) {
}
