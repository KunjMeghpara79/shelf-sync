package shelfsync.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import shelfsync.enums.BookCategory;

public record BookDataRequestDto(
        @NotBlank(message = "book name can not be blank")
        @Pattern(
                regexp = "^[a-zA-Z\\s'-]+$",
                message = "Book name can not be numeric"
        )
        String bookName,

        @NotBlank(message = "author's name can not be blank")
        String authorName,

        @NotNull(message = "Category is required")
        BookCategory bookCategory,

        @Min(value = 1,message = "there should be at least one copy of the book")
        int totalQuantity
) {
}
