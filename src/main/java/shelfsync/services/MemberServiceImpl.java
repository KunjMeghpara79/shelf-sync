package shelfsync.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import shelfsync.enums.LoanStatus;
import shelfsync.enums.MemberStatus;
import shelfsync.exceptions.*;
import shelfsync.mappers.BookDataMapper;
import shelfsync.mappers.LoanMapper;
import shelfsync.models.dto.BookDataResponseDto;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.entities.Book;
import shelfsync.models.entities.BookData;
import shelfsync.models.entities.Loan;
import shelfsync.models.entities.Member;
import shelfsync.repositories.BookDataRepository;
import shelfsync.repositories.BookRepository;
import shelfsync.repositories.LoanRepository;
import shelfsync.repositories.MemberRepository;
import shelfsync.services.interfaces.MemberService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final BookDataRepository bookDataRepository;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final BookDataMapper bookDataMapper;
    private final BookRepository bookRepository;
    @Value("${member.fine.threshold}")
    private int fineThreshold;
    private final String regex = "\\s*-\\s*(?i)copy(\\s+\\d+)?";
    public MemberServiceImpl(MemberRepository memberRepository, BookDataRepository bookDataRepository, LoanRepository loanRepository, LoanMapper loanMapper, BookDataMapper bookDataMapper, BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.bookDataRepository = bookDataRepository;
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.bookDataMapper = bookDataMapper;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public LoanResponseDto borrowBook(int bookId,int memberId){
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book not found!"));
        BookData bookData = book.getBookData();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        if(member.getMemberStatus() == MemberStatus.RESTRICTED) throw new RestrictedAccessException("Member is Restricted!");
        if(member.getLoans().stream()
                .anyMatch(l -> l.getBook().getBookName().replaceAll(regex,"").equals(bookData.getBookName()))){
            throw new BookAlreadyBorrowedException("This member has already borrowed one copy of this book");
        }
        if(bookData.getAvailableQuantity() <= 0) throw new BookNotAvailableException("No copies Available!");
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

    @Override
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
        BookData bookData = bookDataRepository.findBybookName(book.getBookName().replaceAll(regex,"")).orElseThrow(() -> new BookNotFoundException("Book not found!"));
        bookData.setAvailableQuantity(bookData.getAvailableQuantity() + 1);
        LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(loan);
        loanResponseDto = loanResponseDto.withBookName(loan.getBook().getBookName());
        return loanResponseDto;
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void restrictMembers(){
        List<Member> members = memberRepository.findByFineGreaterThanEqualAndMemberStatus(fineThreshold,MemberStatus.ACTIVE);
        for(Member member : members){
            member.setMemberStatus(MemberStatus.RESTRICTED);
        }
    }

    @Override
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

    @Override
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

    @Override
    public List<BookDataResponseDto> getAvailableBooks(){
        List<BookData> bookData = bookDataRepository.findAll();
        bookData = bookData.stream()
                .filter(b -> b.getAvailableQuantity() > 0).toList();
        List<BookDataResponseDto> bookDataResponseDtos= bookData.stream()
                .map(b -> bookDataMapper.bookDatatoBookDataResponseDto(b)).toList();
        return bookDataResponseDtos;
    }

    @Override
    public BookDataResponseDto findByBookName(String bookName) {
        BookData bookData = bookDataRepository.findBybookName(bookName).orElseThrow(() -> new BookNotFoundException("Book not found!"));
        return bookDataMapper.bookDatatoBookDataResponseDto(bookData);
    }

    @Override
    public List<BookDataResponseDto> findByAuthorName(String authorName) {
        List<BookData> bookData = bookDataRepository.findByauthorName(authorName).orElseThrow(() -> new BookNotFoundException("No books available of the author " + authorName));
        return bookData.stream()
                .map(b -> bookDataMapper.bookDatatoBookDataResponseDto(b)).toList();
    }


}
