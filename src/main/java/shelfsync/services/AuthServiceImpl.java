package shelfsync.services;
import shelfsync.exceptions.InvalidPasswordException;
import shelfsync.exceptions.MemberAlreadyExistsException;
import shelfsync.exceptions.MemberNotFoundException;
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
        if(memberRepository.existsByMemberEmail(memberRequestDto.memberEmail())) throw new MemberAlreadyExistsException("Member Already exists!");
        Member member = memberMapper.memberRequestDtoToMember(memberRequestDto);
        member.setPassword(passwordEncoder.encode(memberRequestDto.password()));
        memberRepository.save(member);
        return memberMapper.memberToMemberResponseDto(member);
    }

    @Override
    public JwtResponseDto loginValidation(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByMemberEmail(loginRequestDto.email()).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        if (!passwordEncoder.matches(loginRequestDto.password(),member.getPassword())) {
            throw new InvalidPasswordException("Invalid password!");
        }
        String token = jwtService.generateToken(loginRequestDto.email(),"MEMBER");
        return new JwtResponseDto(token);
    }
}
