package shelfsync.services;

import jakarta.transaction.Transactional;
import org.apache.el.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import shelfsync.enums.BookCategory;
import shelfsync.enums.LoanStatus;
import shelfsync.enums.MemberStatus;
import shelfsync.exceptions.*;
import shelfsync.mappers.BookDataMapper;
import shelfsync.mappers.LoanMapper;
import shelfsync.mappers.ReviewMapper;
import shelfsync.models.dto.*;
import shelfsync.models.entities.*;
import shelfsync.repositories.*;
import shelfsync.services.interfaces.MemberService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.stream;


@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final BookDataRepository bookDataRepository;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final BookDataMapper bookDataMapper;
    private final ReviewMapper reviewMapper;
    @Value("${member.fine.threshold}")
    private int fineThreshold;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public MemberServiceImpl(MemberRepository memberRepository,
                             BookDataRepository bookDataRepository,
                             LoanRepository loanRepository, LoanMapper loanMapper,
                             BookDataMapper bookDataMapper, BookRepository bookRepository,
                             ReviewMapper reviewMapper, ReviewRepository reviewRepository) {
        this.memberRepository = memberRepository;
        this.bookDataRepository = bookDataRepository;
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.bookDataMapper = bookDataMapper;
        this.bookRepository = bookRepository;
        this.reviewMapper = reviewMapper;
        this.reviewRepository = reviewRepository;
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void restrictMembers(){
        List<Member> members = memberRepository.findByFineGreaterThanEqualAndMemberStatus(fineThreshold,MemberStatus.ACTIVE);
        for(Member member : members){
            member.restrictMember();
        }
    }

    @Override
    public List<LoanResponseDto> getLoansReport(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByMemberEmail(email).orElseThrow(() -> new NotFoundException("Member not found!"));
        List<Loan> loans = loanRepository.findByLoanStatusInAndMember(List.of(LoanStatus.DUE,LoanStatus.PENDING),member);
        return loans.stream()
                .map(l -> {
                    LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(l);
                   return loanResponseDto.withBookName(member.getMemberName(),l.getBook().getBookName());
                }).toList();
    }

    @Override
    public List<LoanResponseDto> getLoanHistory(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByMemberEmail(email).orElseThrow(() -> new NotFoundException("Member not found!"));
        List<Loan> loans = loanRepository.findByLoanStatusInAndMember(List.of(LoanStatus.PAID),member);
        return loans.stream()
                .map(l -> {
                    LoanResponseDto loanResponseDto = loanMapper.loanToLoanResponseDto(l);
                    return loanResponseDto.withBookName(member.getMemberName(),l.getBook().getBookName());
                }).toList();
    }

    @Override
    @Transactional
    public List<BookDataResponseDto> getAvailableBooks(){
        List<BookData> bookData = bookDataRepository.findAll();
        bookData = bookData.stream()
                .filter(b -> b.getTotalQuantity() - b.getLoans().size() > 0).toList();
        return bookData.stream()
                .map(b -> bookDataMapper.bookDatatoBookDataResponseDto(b)).toList();
    }

    @Override
    public List<BookDataResponseDto> findByBookName(SearchRequestDto searchRequestDto) {
        List<BookData> bookData = bookDataRepository
                .findBybookNameContainingIgnoreCase(searchRequestDto.name())
                .orElseThrow(() -> new NotFoundException("Book not found!"));
        return bookData.stream()
                .map(b -> bookDataMapper.bookDatatoBookDataResponseDto(b)).toList();
    }

    @Override
    public List<BookDataResponseDto> findByAuthorName(SearchRequestDto searchRequestDto) {
        List<BookData> bookData = bookDataRepository
                .findByauthorNameContainingIgnoreCase(searchRequestDto.name())
                .orElseThrow(() -> new NotFoundException("No books available of the author " + searchRequestDto.name()));
        return bookData.stream()
                .map(b -> bookDataMapper.bookDatatoBookDataResponseDto(b)).toList();
    }

    @Override
    public ReviewResponseDto addBookReview(ReviewRequestDto reviewRequestDto) {
        Book book = bookRepository.findById(reviewRequestDto.bookId()).orElseThrow(() -> new NotFoundException("Book not found !"));
        BookData bookData = book.getBookData();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByMemberEmail(email).orElseThrow(() ->  new NotFoundException("Member not found !"));
        Set<Loan> loanSet = member.getLoans();
        if(loanSet.stream().noneMatch(l -> l.getBook().getBookName() == book.getBookName())){
            throw new ForbiddenException("You can add review of only those books which are borrowed by you !");
        }
        Review review = reviewRepository.findByBookDataAndMember(bookData,member);
        if(review == null){
            review = new Review(reviewRequestDto.rating(),reviewRequestDto.description(),member,bookData);
        }
        else {
            review.setDescription(reviewRequestDto.description());
            review.setRating(reviewRequestDto.rating());
        }
        reviewRepository.save(review);
        return reviewMapper.reviewToReviewResponseDto(review).withBookAndMemberName(book.getBookName(),member.getMemberName());
    }

    @Override
    public List<ReviewResponseDto> getAllReviews() {
        List<Review> list = reviewRepository.findAll();
        return list.stream().map(r -> reviewMapper.reviewToReviewResponseDto(r)).toList();
    }

    @Override
    public List<ReviewResponseDto> getBookReview(SearchRequestDto searchRequestDto) {
        List<BookData> bookDataList = bookDataRepository
                .findBybookNameContainingIgnoreCase(searchRequestDto.name())
                .orElseThrow(() -> new NotFoundException("Book not found !"));
        List<Review> reviews = new ArrayList<>();
        for(BookData bookData : bookDataList){
            reviews.addAll(reviewRepository.findByBookData(bookData));
        }
        return reviews
                .stream()
                .map(r -> reviewMapper.reviewToReviewResponseDto(r)
                        .withBookAndMemberName(r.getBookData().getBookName(),r.getMember().getMemberName())).toList();
    }

    @Override
    public List<BookDataResponseDto> getBookbyCategory(String bookCategory){
        if(stream(BookCategory.values()).noneMatch(e -> e.name().equalsIgnoreCase(bookCategory))){
            throw new ForbiddenException("No match found for the category : " + bookCategory);
        }
        BookCategory bookCategory1 = BookCategory.valueOf(bookCategory.toUpperCase());
        return bookDataRepository.findByBookCategory(bookCategory1).stream().map(bookDataMapper::bookDatatoBookDataResponseDto).toList();
    }



}
