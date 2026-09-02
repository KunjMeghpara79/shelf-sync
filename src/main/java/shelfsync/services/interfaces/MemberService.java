package shelfsync.services.interfaces;

import shelfsync.models.dto.BookDataResponseDto;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.entities.BookData;
import shelfsync.models.entities.Loan;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    public List<LoanResponseDto> getLoansReport();
    public List<LoanResponseDto> getLoanHistory();
    public List<BookDataResponseDto> getAvailableBooks();
    public BookDataResponseDto findByBookName(String bookName);
    public List<BookDataResponseDto> findByAuthorName(String authorName);
}
