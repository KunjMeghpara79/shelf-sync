package shelfsync.services.interfaces;

import shelfsync.models.dto.*;

import java.util.List;

public interface AdminService {
    JwtResponseDto loginValidation(AdminLoginRequestDto adminLoginRequestDto);
    List<LoanResponseDto> getLoansReport();
    List<LoanResponseDto> getMemberLoans(int memberId);
    MemberResponseDto getMember(int memberId);
    MemberResponseDto collectFine(int memberId, int fineAmount);
    BookDataResponseDto addBook(BookDataRequestDto bookDataRequestDto);
    LoanResponseDto issueBook(int bookId, int memberId);
    LoanResponseDto collectBook(int id);
}
