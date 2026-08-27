package shelfsync.Controllers;
import jakarta.validation.Valid;
import shelfsync.Models.DTOs.LoginRequestDto;
import shelfsync.Models.DTOs.MemberRequestDto;
import shelfsync.Models.DTOs.MemberResponseDto;
import shelfsync.Services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MemberResponseDto> registerMember(@RequestBody @Valid MemberRequestDto memberRequestDto){
        MemberResponseDto memberResponseDto = authService.registerMember(memberRequestDto);
        return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginMember(@RequestBody @Valid LoginRequestDto loginRequestDto) throws Exception {
        String jwt = authService.loginValidation(loginRequestDto);
        return new ResponseEntity<>(jwt,HttpStatus.OK);
    }
}
