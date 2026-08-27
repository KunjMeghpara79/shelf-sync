package shelfsync.Models.DTOs;

public record BookDataResponseDto(
        String bookName,
        String authorName,
        int totalQuantity,
        int availableQuantity
) {
}
