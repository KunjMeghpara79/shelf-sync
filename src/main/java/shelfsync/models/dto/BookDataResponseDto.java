package shelfsync.models.dto;

public record BookDataResponseDto(
        String bookName,
        String authorName,
        int totalQuantity
) {
}
