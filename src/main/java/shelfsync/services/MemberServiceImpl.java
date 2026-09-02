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

    public MemberServiceImpl(MemberRepository memberRepository, BookDataRepository bookDataRepository, LoanRepository loanRepository, LoanMapper loanMapper, BookDataMapper bookDataMapper, BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.bookDataRepository = bookDataRepository;
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.bookDataMapper = bookDataMapper;
        this.bookRepository = bookRepository;
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
