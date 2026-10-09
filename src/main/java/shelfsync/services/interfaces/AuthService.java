package shelfsync.services.interfaces;

import shelfsync.models.dto.JwtResponseDto;
import shelfsync.models.dto.LoginRequestDto;
import shelfsync.models.dto.MemberRequestDto;
import shelfsync.models.dto.MemberResponseDto;

public interface AuthService {
    MemberResponseDto registerMember(MemberRequestDto memberRequestDto);
    JwtResponseDto loginValidation(LoginRequestDto loginRequestDto);
}
