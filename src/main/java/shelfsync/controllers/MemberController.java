package shelfsync.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.services.MemberServiceImpl;
import shelfsync.services.interfaces.MemberService;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/late-loans")
    public ResponseEntity<List<LoanResponseDto>> getLateloans(){
        List<LoanResponseDto> loanResponseDtos = memberService.getLoansReport();
        return new ResponseEntity<>(loanResponseDtos,HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<LoanResponseDto>> getMemberHistory(){
        List<LoanResponseDto> loanResponseDtos = memberService.getLoanHistory();
        return new ResponseEntity<>(loanResponseDtos,HttpStatus.OK);
    }
}
