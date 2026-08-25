package shelfsync.Services;

import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shelfsync.Enums.MemberStatus;
import shelfsync.Exceptions.*;
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

    @Transactional
    public LoanResponseDto borrowBook(int id){
        BookData bookData = bookDataRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found!"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByMemberEmail(email).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        String regex = "\\s*-\\s*(?i)copy(\\s+\\d+)?";
        if(member.getLoans().stream()
                .anyMatch(l -> l.getBook().getBookName().replaceAll(regex,"").equals(bookData.getBookName()))){
            throw new BookAlreadyBorrowedException("You have already borrowed one copy of this book.");
        }
        if(bookData.getAvailableQuantity() <= 0) throw new BookNotAvailableException("No copies Available!");
        if(member.getMemberStatus() == MemberStatus.RESTRICTED) throw new RestrictedAccessException("You can not borrow more books!");
        Book book = bookData.getBooks().stream()
                .filter(b -> b.getLoan() == null)
                .sorted((a,b) -> a.getBookId() - b.getBookId())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No copies available for checkout."));
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBook(book);
        loan.setDueDate(loan.getIssueDate().plusDays(5));
        book.setLoan(loan);
        bookData.setAvailableQuantity(bookData.getAvailableQuantity() - 1);
        member.getLoans().add(loan);
        LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(loan);
        loanResponseDto = loanResponseDto.withBookName(loan.getBook().getBookName());
        return loanResponseDto;
    }
}
