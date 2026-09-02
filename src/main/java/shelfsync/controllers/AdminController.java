package shelfsync.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shelfsync.models.dto.*;
import shelfsync.services.MemberServiceImpl;
import shelfsync.services.interfaces.AdminService;
import shelfsync.services.interfaces.MemberService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    public AdminController(AdminService adminService, MemberServiceImpl memberServiceImpl, MemberService memberService) {
        this.adminService = adminService;
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> adminLogin(@RequestBody @Valid AdminLoginRequestDto adminLoginRequestDto) {
        return new ResponseEntity<>(adminService.loginValidation(adminLoginRequestDto),HttpStatus.OK);
    }

    @GetMapping("/reports")
    public ResponseEntity<List<LoanResponseDto>> generateReport() {
        List<LoanResponseDto> loanResponseDtos = adminService.getLoansReport();
        return new ResponseEntity<>(loanResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/reports/{memberId}")
    public ResponseEntity<List<LoanResponseDto>> generateMemberReport(@PathVariable int memberId) {
        List<LoanResponseDto> loanResponseDtos = adminService.getMemberLoans(memberId);
        return new ResponseEntity<>(loanResponseDtos, HttpStatus.OK);
    }

    @PostMapping("/issue-book/{bookId}/{memberId}")
    public ResponseEntity<LoanResponseDto> borrowBook(@PathVariable int bookId, @PathVariable int memberId) {
        LoanResponseDto loanResponseDto = adminService.issueBook(bookId, memberId);
        return new ResponseEntity<>(loanResponseDto, HttpStatus.OK);
    }

    @PostMapping("/accept-book/{bookId}")
    public ResponseEntity<LoanResponseDto> returnBook(@PathVariable int bookId) {
        LoanResponseDto loanResponseDto = adminService.collectBook(bookId);
        return new ResponseEntity<>(loanResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable int memberId) {
        MemberResponseDto memberResponseDto = adminService.getMember(memberId);
        return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);
    }

    @PutMapping("/{memberId}/{fineAmount}")
    public ResponseEntity<MemberResponseDto> finePay(@PathVariable int memberId, @PathVariable int fineAmount) {
        MemberResponseDto memberResponseDto = adminService.payFine(memberId, fineAmount);
        return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);
    }

    @PostMapping("/book")
    public ResponseEntity<BookDataResponseDto> addBook(@RequestBody @Valid BookDataRequestDto bookDataRequestDto) {
        BookDataResponseDto bookDataResponseDto = adminService.addBook(bookDataRequestDto);
        return new ResponseEntity<>(bookDataResponseDto, HttpStatus.OK);
    }

}
