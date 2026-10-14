package shelfsync.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shelfsync.models.dto.*;
import shelfsync.services.interfaces.MemberService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanResponseDto>> getLateloans(){
        List<LoanResponseDto> loanResponseDtos = memberService.getLoansReport();
        if(loanResponseDtos.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(loanResponseDtos,HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<LoanResponseDto>> getMemberHistory(){
        List<LoanResponseDto> loanResponseDtos = memberService.getLoanHistory();
        if(loanResponseDtos.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(loanResponseDtos,HttpStatus.OK);
    }

    @GetMapping("/available-books")
    public ResponseEntity<List<BookDataResponseDto>> getAvailableBooks(){
        List<BookDataResponseDto> bookDataResponseDtos = memberService.getAvailableBooks();
        if(bookDataResponseDtos.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(bookDataResponseDtos,HttpStatus.OK);
    }

    @PostMapping("/find-by-name")
    public ResponseEntity<List<BookDataResponseDto>> findBookByBookName(@Valid @RequestBody SearchRequestDto searchRequestDto){
        List<BookDataResponseDto> bookData = memberService.findByBookName(searchRequestDto);
        if(bookData.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(bookData,HttpStatus.OK);
    }

    @PostMapping("find-by-authorname")
    public ResponseEntity<List<BookDataResponseDto>> findByAuthorname(@RequestBody SearchRequestDto searchRequestDto){
        List<BookDataResponseDto> bookDataResponseDtos = memberService.findByAuthorName(searchRequestDto);
        if(bookDataResponseDtos.isEmpty())return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(bookDataResponseDtos,HttpStatus.OK);
    }

    @PostMapping("add-review")
    public ResponseEntity<ReviewResponseDto> addBookReview(@Valid @RequestBody ReviewRequestDto reviewRequestDto){
        ReviewResponseDto reviewResponseDto = memberService.addBookReview(reviewRequestDto);
        return new ResponseEntity<>(reviewResponseDto,HttpStatus.CREATED);
    }
    @GetMapping("reviews")
    public ResponseEntity<List<ReviewResponseDto>> getAllResponse(){
        List<ReviewResponseDto> list = memberService.getAllReviews();
        if(list.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }
}
