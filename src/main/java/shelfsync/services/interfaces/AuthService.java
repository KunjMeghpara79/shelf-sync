package shelfsync.services.interfaces;

import shelfsync.models.dto.JwtResponseDto;
import shelfsync.models.dto.LoginRequestDto;
import shelfsync.models.dto.MemberRequestDto;
import shelfsync.models.dto.MemberResponseDto;

public interface AuthService {
    public MemberResponseDto registerMember(MemberRequestDto memberRequestDto);
    public JwtResponseDto loginValidation(LoginRequestDto loginRequestDto);
}
