package shelfsync.services;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shelfsync.enums.LoanStatus;
import shelfsync.enums.MemberStatus;
import shelfsync.exceptions.*;
import shelfsync.mappers.BookDataMapper;
import shelfsync.mappers.LoanMapper;
import shelfsync.mappers.MemberMapper;
import shelfsync.models.dto.*;
import shelfsync.models.entities.*;
import shelfsync.repositories.*;
import shelfsync.security.JwtService;
import shelfsync.services.interfaces.AdminService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final BookDataRepository bookDataRepository;
    private final BookDataMapper bookDataMapper;
    private final BookRepository bookRepository;
    private final String regex = "\\s*-\\s*(?i)copy(\\s+\\d+)?";
    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtService jwtService, LoanRepository loanRepository, LoanMapper loanMapper, MemberMapper memberMapper, MemberRepository memberRepository, BookDataRepository bookDataRepository, BookDataMapper bookDataMapper, BookRepository bookRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.memberMapper = memberMapper;
        this.memberRepository = memberRepository;
        this.bookDataRepository = bookDataRepository;
        this.bookDataMapper = bookDataMapper;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public LoanResponseDto issueBook(int bookId, int memberId){
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
    public LoanResponseDto collectBook(int id){
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
    @Override
    public JwtResponseDto loginValidation(AdminLoginRequestDto adminLoginRequestDto){
        Admin admin = adminRepository.findByAdminEmail(adminLoginRequestDto.adminEmail()).orElseThrow(() -> new MemberNotFoundException("Admin not found!"));
        if (!passwordEncoder.matches(adminLoginRequestDto.password(),admin.getPassword())) {
            throw new InvalidPasswordException("Invalid password!");
        }
        String token = jwtService.generateToken(admin.getAdminEmail(),"ADMIN");
        return new JwtResponseDto(token);
    }

    @Override
    public List<LoanResponseDto> getLoansReport(){
        List<Loan> loans = loanRepository.findByLoanStatus(LoanStatus.DUE);
        loans.addAll(loanRepository.findByLoanStatus(LoanStatus.PENDING));

        List<LoanResponseDto> loanResponseDtos = loans.stream()
                .map(l ->{
                    LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(l);
                    return loanResponseDto.withBookName(l.getBook().getBookName());
                }).toList();
        return loanResponseDtos;
    }

    @Override
    public List<LoanResponseDto> getMemberLoans(int memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        List<LoanResponseDto> loanResponseDtos = member.getLoans().stream()
                .map(l -> {
                    LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(l);
                    return loanResponseDto.withBookName(l.getBook().getBookName());
                }).toList();
        return loanResponseDtos;
    }

    @Override
    public MemberResponseDto getMember(int memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        return memberMapper.memberToMemberResponseDto(member);
    }

    @Override
    public MemberResponseDto payFine(int memberId,int fineAmount){
        if(fineAmount <=0 )throw new FinePayException("Amount can not be zero or negative");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        if(member.getFine() < fineAmount) throw new FinePayException("fine amount is exceeding the total fine!");
        member.setFine(member.getFine() - fineAmount);
        memberRepository.save(member);
        return memberMapper.memberToMemberResponseDto(member);
    }

    @Override
    public BookDataResponseDto addBook( BookDataRequestDto bookDataRequestDto){
        BookData bookData = bookDataMapper.bookDataRequestDtoToBookData(bookDataRequestDto);
        bookData.setAvailableQuantity(bookData.getTotalQuantity());
        bookDataRepository.save(bookData);
        for (int j = 1; j <= bookData.getTotalQuantity(); j++) {
            Book book = new Book();
            book.setBookName(bookData.getBookName());
            book.setBookData(bookData);
            book.setLoan(null);
            bookRepository.save(book);
        }

        return bookDataMapper.bookDatatoBookDataResponseDto(bookData);
    }

}
