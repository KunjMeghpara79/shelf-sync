package shelfsync.controllers;
import jakarta.validation.Valid;
import shelfsync.models.dto.JwtResponseDto;
import shelfsync.models.dto.LoginRequestDto;
import shelfsync.models.dto.MemberRequestDto;
import shelfsync.models.dto.MemberResponseDto;
import shelfsync.services.AuthServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shelfsync.services.interfaces.AuthService;

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
    public ResponseEntity<JwtResponseDto> loginMember(@RequestBody @Valid LoginRequestDto loginRequestDto) throws Exception {
        JwtResponseDto jwt = authService.loginValidation(loginRequestDto);
        return new ResponseEntity<>(jwt,HttpStatus.OK);
    }
}
