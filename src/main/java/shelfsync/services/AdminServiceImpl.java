package shelfsync.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shelfsync.borrowchain.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Value("${member.fine.threshold}")
    private int fineThreshold;
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
        if(bookId <=0) throw new InvalidFormatException("book id can not be zero or negative !");
        if(memberId <=0)throw new InvalidFormatException("Member id can not be zero or negative !");
        Optional<Book> book = bookRepository.findById(bookId);
        if(book.isEmpty()) throw new BookNotFoundException("Book not found !");
        BookData bookData = book.get().getBookData();
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.isEmpty()) throw new MemberNotFoundException("Member not found !");
        Request request = new Request(book.get(),bookData,member.get());
        AlreadyBorrowedHandler alreadyBorrowedHandler = new AlreadyBorrowedHandler();
        RestrictedMemberHandler restrictedMemberHandler = new RestrictedMemberHandler();
        DuplicateCopyHandler duplicateCopyHandler = new DuplicateCopyHandler();
        QuantityHandler quantityHandler = new QuantityHandler();
        alreadyBorrowedHandler.setNext(restrictedMemberHandler);
        restrictedMemberHandler.setNext(duplicateCopyHandler);
        duplicateCopyHandler.setNext(quantityHandler);
        alreadyBorrowedHandler.check(request);
        Loan loan = new Loan();
        loan.setMember(member.get());
        loan.setBook(book.get());
        loan.setDueDate(loan.getIssueDate().plusDays(5));
        book.get().setLoan(loan);
        bookData.getLoans().add(loan);
        member.get().getLoans().add(loan);
        LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(loan);
        loanResponseDto = loanResponseDto.withBookName(member.get().getMemberName(),loan.getBook().getBookName());
        return loanResponseDto;
    }

    @Override
    @Transactional
    public LoanResponseDto collectBook(int id){
        if(id <=0) throw new InvalidFormatException("Book id can not be zero or negative !");
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found !"));
        if(book.getLoan() == null) throw new LoanNotFoundException("No loan found for this book !");
        Member member = book.getLoan().getMember();
        Loan loan = member.getLoans().stream()
                .filter(l -> l.getBook().getBookId() == book.getBookId()).findFirst().orElseThrow(() -> new LoanNotFoundException("Loan not found!"));
        LocalDateTime returnTime = LocalDateTime.now(ZoneId.of("UTC"));
        loan.setLoanStatus(LoanStatus.PAID);
        loan.setReturnDate(returnTime);
        book.setLoan(null);
        BookData bookData = book.getBookData();
        bookData.getLoans().remove(loan);
        LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(loan);
        loanResponseDto = loanResponseDto.withBookName(member.getMemberName(),loan.getBook().getBookName());
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
        List<Loan> loans = loanRepository.findByLoanStatusIn(List.of(LoanStatus.DUE,LoanStatus.PENDING));
        return loans.stream()
                .map(l ->{
                    LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(l);
                    return loanResponseDto.withBookName(l.getMember().getMemberName(),l.getBook().getBookName());
                }).toList();
    }

    @Override
    public List<LoanResponseDto> getMemberLoans(int memberId){
        if(memberId <=0) throw new InvalidFormatException("Member id can not be zero or negative !");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        return member.getLoans().stream()
                .map(l -> {
                    LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(l);
                    return loanResponseDto.withBookName(member.getMemberName(),l.getBook().getBookName());
                }).toList();
    }

    @Override
    public MemberResponseDto getMember(int memberId){
        if(memberId <=0) throw new InvalidFormatException("Member id can not be zero or negative !");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        return memberMapper.memberToMemberResponseDto(member);
    }

    @Override
    public MemberResponseDto collectFine(int memberId, int fineAmount){
        if(memberId <=0) throw new InvalidFormatException("Member id can not be zero or negative !");
        if(fineAmount <=0 )throw new FinePayException("Amount can not be zero or negative");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        if(member.getFine() <= 0) throw new FinePayException("Member have no fine to pay!");
        if(member.getFine() < fineAmount) throw new FinePayException("fine amount is exceeding the total fine!");
        if(member.getLoans().stream().anyMatch(l -> l.getLoanStatus() == LoanStatus.DUE)){throw new RestrictedAccessException("You have to complete all late book returns first then you can pay fine !");}
        Loan loan = member.getLoans().stream().filter(l -> l.getFine() == fineAmount).findFirst().orElseThrow(() -> new LoanNotFoundException("No loan found of " + fineAmount + " Rs !"));
        loan.setFine(0);
        member.setFine(member.getFine() - fineAmount);
        if(member.getFine() < fineThreshold) member.setMemberStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);
        return memberMapper.memberToMemberResponseDto(member);
    }

    @Override
    public BookDataResponseDto addBook( BookDataRequestDto bookDataRequestDto){
        BookData bookData = bookDataMapper.bookDataRequestDtoToBookData(bookDataRequestDto);
        bookDataRepository.save(bookData);
        List<Book> books = new ArrayList<>();
        for (int j = 1; j <= bookData.getTotalQuantity(); j++) {
            Book book = new Book();
            book.setBookName(bookData.getBookName());
            book.setBookData(bookData);
            book.setLoan(null);
            books.add(book);
        }
        bookRepository.saveAll(books);
        return bookDataMapper.bookDatatoBookDataResponseDto(bookData);
    }

}
