package shelfsync.models.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

public record ReviewRequestDto(
        @DecimalMin(value = "0.1",message = "Rating value should be in the range of 1 to 5")
        @DecimalMax(value = "5.0",message = "Rating value should be in the range of 1 to 5")
        double rating,
        @Nullable
        String description,
        @Min(value = 1,message = "Book id can not be zero or negative")
        int bookId
) {
}
