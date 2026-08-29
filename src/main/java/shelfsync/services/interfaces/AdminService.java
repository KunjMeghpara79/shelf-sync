package shelfsync.services.interfaces;

import shelfsync.models.dto.*;

import java.util.List;

public interface AdminService {
    public JwtResponseDto loginValidation(AdminLoginRequestDto adminLoginRequestDto);
    public List<LoanResponseDto> getLoansReport();
    public List<LoanResponseDto> getMemberLoans(int memberId);
    public MemberResponseDto getMember(int memberId);
    public MemberResponseDto payFine(int memberId,int fineAmount);
    public BookDataResponseDto addBook(BookDataRequestDto bookDataRequestDto);
}
