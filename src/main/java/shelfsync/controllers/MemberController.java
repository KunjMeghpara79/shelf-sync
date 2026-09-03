package shelfsync.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shelfsync.models.dto.BookDataResponseDto;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.dto.SearchRequestDto;
import shelfsync.models.entities.BookData;
import shelfsync.services.MemberServiceImpl;
import shelfsync.services.interfaces.MemberService;

import java.awt.print.Book;
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
        return new ResponseEntity<>(loanResponseDtos,HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<LoanResponseDto>> getMemberHistory(){
        List<LoanResponseDto> loanResponseDtos = memberService.getLoanHistory();
        return new ResponseEntity<>(loanResponseDtos,HttpStatus.OK);
    }

    @GetMapping("/available-books")
    public ResponseEntity<List<BookDataResponseDto>> getAvailableBooks(){
        List<BookDataResponseDto> bookDataResponseDtos = memberService.getAvailableBooks();
        return new ResponseEntity<>(bookDataResponseDtos,HttpStatus.OK);
    }

    @PostMapping("/find-by-name")
    public ResponseEntity<List<BookDataResponseDto>> findBookByBookName(@RequestBody SearchRequestDto searchRequestDto){
        List<BookDataResponseDto> bookData = memberService.findByBookName(searchRequestDto);
        return new ResponseEntity<>(bookData,HttpStatus.OK);
    }

    @PostMapping("find-by-authorname")
    public ResponseEntity<List<BookDataResponseDto>> findByAuthorname(@RequestBody SearchRequestDto searchRequestDto){
        List<BookDataResponseDto> bookDataResponseDtos = memberService.findByAuthorName(searchRequestDto);
        return new ResponseEntity<>(bookDataResponseDtos,HttpStatus.OK);
    }
}
