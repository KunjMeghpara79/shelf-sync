package shelfsync.mappers;

import org.mapstruct.Mapper;
import shelfsync.models.dto.ReviewResponseDto;
import shelfsync.models.entities.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewResponseDto reviewToReviewResponseDto(Review review);
}
