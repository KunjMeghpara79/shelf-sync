package shelfsync.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shelfsync.models.dto.*;
import shelfsync.services.interfaces.AdminService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> adminLogin(@RequestBody @Valid AdminLoginRequestDto adminLoginRequestDto) {
        return new ResponseEntity<>(adminService.loginValidation(adminLoginRequestDto),HttpStatus.OK);
    }

    @GetMapping("/reports")
    public ResponseEntity<List<LoanResponseDto>> generateReport() {
        List<LoanResponseDto> loanResponseDtos = adminService.getLoansReport();
        if(loanResponseDtos.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(loanResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/reports/{memberId}")
    public ResponseEntity<List<LoanResponseDto>> generateMemberReport(@PathVariable String memberId) {
        if (!memberId.matches("[0-9]+")) {
            throw new IllegalArgumentException("Member ID must contain digits only");
        }
        int id = Integer.parseInt(memberId);
        List<LoanResponseDto> loanResponseDtos = adminService.getMemberLoans(id);
        if(loanResponseDtos.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(loanResponseDtos, HttpStatus.OK);
    }

    @PostMapping("/issue-book/{bookId}/{memberId}")
    public ResponseEntity<LoanResponseDto> borrowBook(@PathVariable String bookId, @PathVariable String memberId) {
        if(!bookId.matches("[0-9]+")){
            throw new IllegalArgumentException("Book id can contain digits only !");
        }
        int bId = Integer.parseInt(bookId);
        if(!memberId.matches("[0-9]+")){
            throw new IllegalArgumentException("Member id can contain digits only !");
        }
        int mId = Integer.parseInt(memberId);
        LoanResponseDto loanResponseDto = adminService.issueBook(bId, mId);
        return new ResponseEntity<>(loanResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/accept-book/{bookId}")
    public ResponseEntity<LoanResponseDto> returnBook(@PathVariable String bookId) {
        if(!bookId.matches("[0-9]+")){
            throw new IllegalArgumentException("Book id can contain digits only !");
        }
        int bId = Integer.parseInt(bookId);
        LoanResponseDto loanResponseDto = adminService.collectBook(bId);
        return new ResponseEntity<>(loanResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable String memberId) {
        if (!memberId.matches("[0-9]+")) {
            throw new IllegalArgumentException("Member ID must contain digits only");
        }
        int id = Integer.parseInt(memberId);
        MemberResponseDto memberResponseDto = adminService.getMember(id);
        return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);
    }

    @PutMapping("/fine/{bookId}/{fineAmount}")
    public ResponseEntity<MemberResponseDto> finePay(@PathVariable String bookId, @PathVariable int fineAmount) {
        if(!bookId.matches("[0-9]+")){
            throw new IllegalArgumentException("Book id can contain digits only !");
        }
        int bId = Integer.parseInt(bookId);
        MemberResponseDto memberResponseDto = adminService.collectFine(bId, fineAmount);
        return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);
    }

    @PostMapping("/book")
    public ResponseEntity<BookDataResponseDto> addBook(@RequestBody @Valid BookDataRequestDto bookDataRequestDto) {
        BookDataResponseDto bookDataResponseDto = adminService.addBook(bookDataRequestDto);
        return new ResponseEntity<>(bookDataResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("/fine/collect-total/{memberId}/{fineAmount}")
    public ResponseEntity<MemberResponseDto> collectTotalFine(@PathVariable String memberId,@PathVariable int fineAmount){
        if(!memberId.matches("[0-9]+")){
            throw new IllegalArgumentException("Member id can contain digits only !");
        }
        int mId = Integer.parseInt(memberId);
        MemberResponseDto memberResponseDto = adminService.collectTotalFine(mId,fineAmount);
        return new ResponseEntity<>(memberResponseDto,HttpStatus.OK);
    }
    @PutMapping("book/renew/{bookId}")
    public ResponseEntity<LoanResponseDto> renewBook(@PathVariable String bookId){
        if(!bookId.matches("[0-9]+")){
            throw new IllegalArgumentException("Book id can contain digits only !");
        }
        int bId = Integer.parseInt(bookId);
        LoanResponseDto loanResponseDto = adminService.renewLoan(bId);
        return new ResponseEntity<>(loanResponseDto,HttpStatus.OK);
    }
}
