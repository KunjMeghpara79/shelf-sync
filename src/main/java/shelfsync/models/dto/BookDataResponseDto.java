package shelfsync.models.dto;

import shelfsync.enums.BookCategory;

public record BookDataResponseDto(
        String bookName,
        String authorName,
        int totalQuantity,
        BookCategory bookCategory
) {
}
