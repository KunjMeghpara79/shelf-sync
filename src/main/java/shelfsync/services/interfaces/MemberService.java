package shelfsync.services.interfaces;

import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.entities.Loan;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    public LoanResponseDto borrowBook(int bookId, int memberId);
    public LoanResponseDto returnBook(int id);
    public List<LoanResponseDto> getLoansReport();
    public List<LoanResponseDto> getLoanHistory();
}
