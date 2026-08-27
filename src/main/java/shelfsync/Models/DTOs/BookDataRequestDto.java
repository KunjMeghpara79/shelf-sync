package shelfsync.Models.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.node.StringNode;

public record BookDataRequestDto(
        @NotBlank(message = "book name can not be blank")
        String bookName,

        @NotBlank(message = "author's name can not be null")
        String authorName,

        @Min(value = 1,message = "there should be at least one copy of the book")
        int totalQuantity
) {
}
