package shelfsync.Services;
import shelfsync.Exceptions.InvalidPasswordException;
import shelfsync.Exceptions.MemberAlreadyExistsException;
import shelfsync.Exceptions.MemberNotFoundException;
import shelfsync.Mappers.MemberMapper;
import shelfsync.Models.DTOs.LoginRequestDto;
import shelfsync.Models.DTOs.MemberRequestDto;
import shelfsync.Models.DTOs.MemberResponseDto;
import shelfsync.Models.Entities.Member;
import shelfsync.Repositories.MemberRepository;
import shelfsync.Security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(MemberMapper memberMapper, MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.memberMapper = memberMapper;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public MemberResponseDto registerMember(MemberRequestDto memberRequestDto){
        if(memberRepository.existsByMemberEmail(memberRequestDto.memberEmail())) throw new MemberAlreadyExistsException("Member Already exists!");
        Member member = memberMapper.memberRequestDtoToMember(memberRequestDto);
        member.setPassword(passwordEncoder.encode(memberRequestDto.password()));
        memberRepository.save(member);
        return memberMapper.memberToMemberResponseDto(member);
    }

    public String loginValidation(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByMemberEmail(loginRequestDto.email()).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        if (!passwordEncoder.matches(loginRequestDto.password(),member.getPassword())) {
            throw new InvalidPasswordException("Invalid password!");
        }
        // 3. Generate token if credentials match
        return jwtService.generateToken(loginRequestDto.email());
    }
}
