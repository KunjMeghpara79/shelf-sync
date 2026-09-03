package shelfsync.services.interfaces;

import shelfsync.models.dto.BookDataResponseDto;
import shelfsync.models.dto.LoanResponseDto;

import java.util.List;

public interface MemberService {
    public List<LoanResponseDto> getLoansReport();
    public List<LoanResponseDto> getLoanHistory();
    public List<BookDataResponseDto> getAvailableBooks();
    public List<BookDataResponseDto> findByBookName(String bookName);
    public List<BookDataResponseDto> findByAuthorName(String authorName);
}
