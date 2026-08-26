package shelfsync.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shelfsync.Models.DTOs.LoanResponseDto;
import shelfsync.Services.MemberService;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @PostMapping("/borrow/{id}")
    public ResponseEntity<LoanResponseDto> borrowBook(@PathVariable int id){
        LoanResponseDto loanResponseDto = memberService.borrowBook(id);
        return new ResponseEntity<>(loanResponseDto, HttpStatus.OK);
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<LoanResponseDto> returnBook(@PathVariable int id){
        LoanResponseDto loanResponseDto = memberService.returnBook(id);
        return new ResponseEntity<>(loanResponseDto,HttpStatus.OK);
    }

    @GetMapping("/late-loans")
    public ResponseEntity<List<LoanResponseDto>> getLateloans(){
        List<LoanResponseDto> loanResponseDtos = memberService.getLateLoansReport();
        return new ResponseEntity<>(loanResponseDtos,HttpStatus.OK);
    }
}
