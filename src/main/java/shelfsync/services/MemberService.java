package shelfsync.services;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import shelfsync.enums.LoanStatus;
import shelfsync.enums.MemberStatus;
import shelfsync.exceptions.*;
import shelfsync.mappers.LoanMapper;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.entities.Book;
import shelfsync.models.entities.BookData;
import shelfsync.models.entities.Loan;
import shelfsync.models.entities.Member;
import shelfsync.repositories.BookDataRepository;
import shelfsync.repositories.BookRepository;
import shelfsync.repositories.LoanRepository;
import shelfsync.repositories.MemberRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BookDataRepository bookDataRepository;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final BookRepository bookRepository;
    private final String regex = "\\s*-\\s*(?i)copy(\\s+\\d+)?";
    public MemberService(MemberRepository memberRepository, BookDataRepository bookDataRepository, LoanRepository loanRepository, LoanMapper loanMapper, BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.bookDataRepository = bookDataRepository;
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public LoanResponseDto borrowBook(int bookId,int memberId){
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book not found!"));
        BookData bookData = book.getBookData();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        if(member.getLoans().stream()
                .anyMatch(l -> l.getBook().getBookName().replaceAll(regex,"").equals(bookData.getBookName()))){
            throw new BookAlreadyBorrowedException("This member has already borrowed one copy of this book");
        }
        if(bookData.getAvailableQuantity() <= 0) throw new BookNotAvailableException("No copies Available!");
        if(member.getMemberStatus() == MemberStatus.RESTRICTED) throw new RestrictedAccessException("Member is Restricted!");
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBook(book);
        loan.setBookData(bookData);
        loan.setDueDate(loan.getIssueDate().plusDays(5));
        book.setLoan(loan);
        bookData.setAvailableQuantity(bookData.getAvailableQuantity() - 1);
        member.getLoans().add(loan);
        LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(loan);
        loanResponseDto = loanResponseDto.withBookName(loan.getBook().getBookName());
        return loanResponseDto;
    }

    @Transactional
    public LoanResponseDto returnBook(int id){
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found!"));
        Member member = book.getLoan().getMember();
        if(!member.getLoans().stream()
                .anyMatch(l -> l.getBook().getBookId() == id)){
            throw new BookNotAvailableException("You have not borrowed this book!");
        }

        Loan loan = member.getLoans().stream()
                .filter(l -> l.getBook().getBookId() == book.getBookId()).findFirst().orElseThrow(() -> new LoanNotFoundException("Loan not found!"));
        LocalDateTime returnTime = LocalDateTime.now(ZoneId.of("UTC"));
        loan.setLoanStatus(LoanStatus.PAID);
        loan.setReturnDate(returnTime);
        book.setLoan(null);
        BookData bookData = bookDataRepository.findBybookName(book.getBookName().replaceAll(regex,""));
        bookData.setAvailableQuantity(bookData.getAvailableQuantity() + 1);
        LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(loan);
        loanResponseDto = loanResponseDto.withBookName(loan.getBook().getBookName());
        return loanResponseDto;
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void restrictMembers(){
        List<Member> members = memberRepository.findByFineGreaterThanEqualAndMemberStatus(30,MemberStatus.ACTIVE);
        for(Member member : members){
            member.setMemberStatus(MemberStatus.RESTRICTED);
        }
    }

    public List<LoanResponseDto> getLoansReport(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByMemberEmail(email).orElseThrow(() -> new MemberNotFoundException("Member not found!"));

        List<Loan> loans = loanRepository.findByLoanStatusAndMember(LoanStatus.DUE,member);
        loans.addAll(loanRepository.findByLoanStatusAndMember(LoanStatus.PENDING,member));

        List<LoanResponseDto> loanResponseDtos = loans.stream()
                .map(l -> {
                    LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(l);
                   return loanResponseDto.withBookName(l.getBook().getBookName());

                }).toList();
        return loanResponseDtos;
    }

    public List<LoanResponseDto> getLoanHistory(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByMemberEmail(email).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        List<Loan> loans = loanRepository.findByLoanStatusAndMember(LoanStatus.PAID,member);
        List<LoanResponseDto> loanResponseDtos = loans.stream()
                .map(l -> {
                    LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(l);
                    return loanResponseDto.withBookName(l.getBook().getBookName());

                }).toList();
        return loanResponseDtos;
    }
}
