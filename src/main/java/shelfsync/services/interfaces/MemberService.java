package shelfsync.services.interfaces;

import shelfsync.models.dto.*;

import java.util.List;

public interface MemberService {
    List<LoanResponseDto> getLoansReport();
    List<LoanResponseDto> getLoanHistory();
    List<BookDataResponseDto> getAvailableBooks();
    List<BookDataResponseDto> findByBookName(SearchRequestDto searchRequestDto);
    List<BookDataResponseDto> findByAuthorName(SearchRequestDto searchRequestDto);
    ReviewResponseDto addBookReview(ReviewRequestDto reviewRequestDto);
    List<ReviewResponseDto> getAllReviews();
    List<ReviewResponseDto> getBookReview(SearchRequestDto searchRequestDto);
}
