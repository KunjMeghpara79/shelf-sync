package shelfsync.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shelfsync.Models.DTOs.LoanResponseDto;
import shelfsync.Services.MemberService;

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
}
