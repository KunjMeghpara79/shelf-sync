package shelfsync.services.interfaces;

import shelfsync.models.dto.BookDataResponseDto;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.dto.SearchRequestDto;

import java.util.List;

public interface MemberService {
    public List<LoanResponseDto> getLoansReport();
    public List<LoanResponseDto> getLoanHistory();
    public List<BookDataResponseDto> getAvailableBooks();
    public List<BookDataResponseDto> findByBookName(SearchRequestDto searchRequestDto);
    public List<BookDataResponseDto> findByAuthorName(SearchRequestDto searchRequestDto);
}
