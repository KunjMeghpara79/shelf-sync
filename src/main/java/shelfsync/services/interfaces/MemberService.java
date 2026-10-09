package shelfsync.services.interfaces;

import shelfsync.models.dto.BookDataResponseDto;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.dto.SearchRequestDto;

import java.util.List;

public interface MemberService {
    List<LoanResponseDto> getLoansReport();
    List<LoanResponseDto> getLoanHistory();
    List<BookDataResponseDto> getAvailableBooks();
    List<BookDataResponseDto> findByBookName(SearchRequestDto searchRequestDto);
    List<BookDataResponseDto> findByAuthorName(SearchRequestDto searchRequestDto);
}
