package shelfsync.Mappers;

import org.mapstruct.Mapper;
import shelfsync.Models.DTOs.BookDataRequestDto;
import shelfsync.Models.DTOs.BookDataResponseDto;
import shelfsync.Models.Entities.BookData;

@Mapper(componentModel = "spring")
public interface BookDataMapper {

    public BookData bookDataRequestDtoToBookData(BookDataRequestDto bookDataRequestDto);

    public BookDataResponseDto bookDatatoBookDataResponseDto(BookData bookData);

}
