package shelfsync.services;
import shelfsync.exceptions.ConflictException;
import shelfsync.exceptions.NotFoundException;
import shelfsync.exceptions.UnauthorizedException;
import shelfsync.mappers.MemberMapper;
import shelfsync.models.dto.JwtResponseDto;
import shelfsync.models.dto.LoginRequestDto;
import shelfsync.models.dto.MemberRequestDto;
import shelfsync.models.dto.MemberResponseDto;
import shelfsync.models.entities.Member;
import shelfsync.repositories.MemberRepository;
import shelfsync.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shelfsync.services.interfaces.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthServiceImpl(MemberMapper memberMapper, MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.memberMapper = memberMapper;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public MemberResponseDto registerMember(MemberRequestDto memberRequestDto){
        if(memberRepository.existsByMemberEmail(memberRequestDto.memberEmail())) throw new ConflictException("Member Already exists!");
        Member member = memberMapper.memberRequestDtoToMember(memberRequestDto);
        member.setPassword(passwordEncoder.encode(memberRequestDto.password()));
        memberRepository.save(member);
        return memberMapper.memberToMemberResponseDto(member);
    }

    @Override
    public JwtResponseDto loginValidation(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByMemberEmail(loginRequestDto.email()).orElseThrow(() -> new NotFoundException("Member not found!"));
        if (!passwordEncoder.matches(loginRequestDto.password(),member.getPassword())) {
            throw new UnauthorizedException("Invalid password!");
        }
        String token = jwtService.generateToken(loginRequestDto.email(),"MEMBER");
        return new JwtResponseDto(token);
    }
}
