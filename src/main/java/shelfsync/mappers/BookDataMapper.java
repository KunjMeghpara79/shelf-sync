package shelfsync.mappers;

import org.mapstruct.Mapper;
import shelfsync.models.dto.BookDataRequestDto;
import shelfsync.models.dto.BookDataResponseDto;
import shelfsync.models.entities.BookData;

@Mapper(componentModel = "spring")
public interface BookDataMapper {

    public BookData bookDataRequestDtoToBookData(BookDataRequestDto bookDataRequestDto);

    public BookDataResponseDto bookDatatoBookDataResponseDto(BookData bookData);

}
