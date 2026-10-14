package shelfsync.models.dto;

import jakarta.annotation.Nullable;

public record ReviewResponseDto(
        String bookName,
        String memberName,
        double rating,
        @Nullable
        String description
) {
        public ReviewResponseDto withBookAndMemberName(String bookName,String memberName) {
                return new ReviewResponseDto(
                        bookName,
                        memberName,
                        this.rating,
                        this.description
                );
        }
}
