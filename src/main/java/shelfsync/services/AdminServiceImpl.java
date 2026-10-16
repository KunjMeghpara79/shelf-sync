package shelfsync.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
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
import shelfsync.services.interfaces.EmailService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static shelfsync.enums.LoanStatus.DUE;

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
    private final EmailService emailService;
    @Value("${member.fine.threshold}")
    private int fineThreshold;
    @Value("${member.borrow.threshold}")
    private int borrowingThreshold;
    private String message = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">
            <h2 style="color: #2e7d32;">Book Borrowing Confirmation</h2>

            <p>Hello <strong>%s</strong>,</p>

            <p>Your book has been issued successfully!</p>

            <p>
                <strong>Book Name:</strong> %s
            </p>

            <p>Please make sure to return the book before the due date.</p>

            <p>Happy Reading!</p>

            <p>
                Regards,<br>
                <strong>Team ShelfSync</strong>
            </p>
        </body>
        </html>
        """;
    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
                            JwtService jwtService, LoanRepository loanRepository,
                            LoanMapper loanMapper, MemberMapper memberMapper,
                            MemberRepository memberRepository, BookDataRepository bookDataRepository,
                            BookDataMapper bookDataMapper, BookRepository bookRepository, EmailService emailService) {
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
        this.emailService = emailService;
    }

    private void sendEmail(EmailDetails emailDetails){
        emailService.sendSimpleMail(emailDetails);
    }
    @Override
    @Transactional
    public LoanResponseDto issueBook(int bookId, int memberId){
        if(bookId <=0) throw new InvalidFormatException("book id can not be zero or negative !");
        if(memberId <=0)throw new InvalidFormatException("Member id can not be zero or negative !");
        Optional<Book> book = bookRepository.findById(bookId);
        if(book.isEmpty()) throw new NotFoundException("Book not found !");
        BookData bookData = book.get().getBookData();
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.isEmpty()) throw new NotFoundException("Member not found !");
        if(member.get().getLoans()
                .stream()
                .filter(l -> l.getLoanStatus() == LoanStatus.PENDING || l.getLoanStatus() == DUE)
                .toList().size() >= borrowingThreshold){
            throw new ForbiddenException("This member has exceeded the borrowing limit !");
        }
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
        String emailMessage = message.formatted(
                member.get().getMemberName(),
                bookData.getBookName()
        );

        EmailDetails emailDetails = new EmailDetails(
                member.get().getMemberEmail(),
                emailMessage,
                "Book Borrowing Confirmation",
                null
        );
        sendEmail(emailDetails);
        return loanResponseDto;
    }

    @Override
    @Transactional
    public LoanResponseDto collectBook(int id){
        if(id <=0) throw new InvalidFormatException("Book id can not be zero or negative !");
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found !"));
        if(book.getLoan() == null) throw new NotFoundException("No loan found for this book !");
        if(LocalDateTime.now(ZoneId.of("UTC")).isAfter(book.getLoan().getDueDate())){
            throw new ForbiddenException("The loan is expired return the book with fine !");
        }
        Member member = book.getLoan().getMember();
        Loan loan = member.getLoans().stream()
                .filter(l -> l.getBook().getBookId() == book.getBookId()).findFirst().orElseThrow(() -> new NotFoundException("Loan not found!"));
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
    @Transactional
    public MemberResponseDto collectTotalFine(int memberId, int fineAmount) {
        if(fineAmount <=0 )throw new InvalidFormatException("Fine amount can not be zero or negative");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("Member not found !"));
        if(member.getFine() == fineAmount){
            for(Loan loan : member.getLoans().stream().filter(l -> l.getLoanStatus() == DUE).toList()){
                loan.setReturnDate(LocalDateTime.now(ZoneId.of("UTC")));
                loan.setLoanStatus(LoanStatus.PAID);
            }
            member.setFine(0);
        }else throw new ForbiddenException("Total fine for the member is not equal to fine amount !");
        return memberMapper.memberToMemberResponseDto(member);
    }

    @Override
    @Transactional
    public LoanResponseDto renewLoan(int bookId) {
        if(bookId <=0 )throw new InvalidFormatException("Book id can not be zero or negative");
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found !"));
        Loan loan = book.getLoan();
        if(loan == null) throw new NotFoundException("No loan found for the book "+ book.getBookName());
        long diff = ChronoUnit.DAYS.between(loan.getIssueDate(), loan.getDueDate());
        if(loan.getLoanStatus() == DUE || loan.getLoanStatus() == LoanStatus.PAID) throw new ForbiddenException("Only pending loans can be renewed");
        if(diff > 5) throw new ForbiddenException("The book can be renewed only once !");
        else{
            loan.setDueDate(loan.getDueDate().plusDays(5));
        }
        return loanMapper.loanToLoanResponseDto(loan);
    }

    @Override
    public JwtResponseDto loginValidation(AdminLoginRequestDto adminLoginRequestDto){
        Admin admin = adminRepository.findByAdminEmail(adminLoginRequestDto.adminEmail()).orElseThrow(() -> new NotFoundException("Admin not found!"));
        if (!passwordEncoder.matches(adminLoginRequestDto.password(),admin.getPassword())) {
            throw new ForbiddenException("Invalid password!");
        }
        String token = jwtService.generateToken(admin.getAdminEmail(),"ADMIN");
        return new JwtResponseDto(token);
    }

    @Override
    public List<LoanResponseDto> getLoansReport(){
        List<Loan> loans = loanRepository.findByLoanStatusIn(List.of(DUE,LoanStatus.PENDING));
        return loans.stream()
                .map(l ->
                    loanMapper.loanToLoanResponseDto(l).withBookName(l.getMember().getMemberName(),l.getBook().getBookName())
                ).toList();
    }

    @Override
    public List<LoanResponseDto> getMemberLoans(int memberId){
        if(memberId <=0) throw new InvalidFormatException("Member id can not be zero or negative !");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("Member not found!"));
        return member.getLoans().stream()
                .map(l -> {
                    LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(l);
                    return loanResponseDto.withBookName(member.getMemberName(),l.getBook().getBookName());
                }).toList();
    }

    @Override
    public MemberResponseDto getMember(int memberId){
        if(memberId <=0) throw new InvalidFormatException("Member id can not be zero or negative !");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("Member not found!"));
        return memberMapper.memberToMemberResponseDto(member);
    }

    @Override
    @Transactional
    public MemberResponseDto collectFine(int bookId, int fineAmount){
        if(bookId <=0) throw new InvalidFormatException("Book id can not be zero or negative !");
        if(fineAmount <=0 )throw new InvalidFormatException("Amount can not be zero or negative");
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found !"));
        if(book.getLoan() == null) throw new NotFoundException("No loan found for this book !");
        if(book.getLoan().getLoanStatus() == LoanStatus.PAID || book.getLoan().getFine() <= 0) {
            throw new NotFoundException("There is no fine on this book or fine on this loan is already paid");
        }
        Member member = book.getLoan().getMember();
        if(member.getFine() <= 0) throw new ForbiddenException("Member have no fine to pay!");
        if(member.getFine() < fineAmount) throw new ForbiddenException("fine amount is exceeding the total fine!");
        Loan loan = book.getLoan();
        if(loan.getFine() != fineAmount) throw new ForbiddenException("Fine amount is not equal to book's fine amount !");
        loan.setLoanStatus(LoanStatus.PAID);
        book.setLoan(null);
        member.setFine(member.getFine() - fineAmount);
        if (member.getFine() < fineThreshold) member.setMemberStatus(MemberStatus.ACTIVE);
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
