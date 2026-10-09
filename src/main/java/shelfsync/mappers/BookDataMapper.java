package shelfsync.mappers;

import org.mapstruct.Mapper;
import shelfsync.models.dto.BookDataRequestDto;
import shelfsync.models.dto.BookDataResponseDto;
import shelfsync.models.entities.BookData;

@Mapper(componentModel = "spring")
public interface BookDataMapper {

    BookData bookDataRequestDtoToBookData(BookDataRequestDto bookDataRequestDto);

    BookDataResponseDto bookDatatoBookDataResponseDto(BookData bookData);

}
