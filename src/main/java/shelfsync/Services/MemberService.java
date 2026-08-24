package shelfsync.Services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import shelfsync.Enums.MemberStatus;
import shelfsync.Exceptions.MemberNotFoundException;
import shelfsync.Mappers.LoanMapper;
import shelfsync.Models.DTOs.LoanResponseDto;
import shelfsync.Models.Entities.Book;
import shelfsync.Models.Entities.BookData;
import shelfsync.Models.Entities.Loan;
import shelfsync.Models.Entities.Member;
import shelfsync.Repositories.BookDataRepository;
import shelfsync.Repositories.BookRepository;
import shelfsync.Repositories.LoanRepository;
import shelfsync.Repositories.MemberRepository;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BookDataRepository bookDataRepository;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final BookRepository bookRepository;
    public MemberService(MemberRepository memberRepository, BookDataRepository bookDataRepository, LoanRepository loanRepository, LoanMapper loanMapper, BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.bookDataRepository = bookDataRepository;
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.bookRepository = bookRepository;
    }

    public LoanResponseDto borrowBook(int id){
        BookData bookData = bookDataRepository.findById(id).orElseThrow();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByMemberEmail(email).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        if(bookData.getAvailableQuantity() <= 0) throw new RuntimeException();
        if(member.getMemberStatus() == MemberStatus.RESTRICTED) throw new RuntimeException();
        Book book = bookData.getBooks().stream()
                .filter(b -> b.getLoan() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No copies available for checkout."));
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBook(book);
        loan.setDueDate(loan.getIssueDate().plusDays(5));
        loanRepository.save(loan);
        book.setLoan(loan);
        bookRepository.save(book);
        bookData.setAvailableQuantity(bookData.getAvailableQuantity() - 1);
        member.getLoans().add(loan);
        LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(loan);
        loanResponseDto = loanResponseDto.withBookName(loan.getBook().getBookName());
        return loanResponseDto;
    }
}
