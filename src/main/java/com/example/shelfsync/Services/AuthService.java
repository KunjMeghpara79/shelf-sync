package com.example.shelfsync.Services;
import com.example.shelfsync.Exceptions.InvalidPasswordException;
import com.example.shelfsync.Exceptions.MemberAlreadyExistsException;
import com.example.shelfsync.Exceptions.MemberNotFoundException;
import com.example.shelfsync.Mappers.MemberMapper;
import com.example.shelfsync.Models.DTOs.LoginRequestDto;
import com.example.shelfsync.Models.DTOs.MemberRequestDto;
import com.example.shelfsync.Models.DTOs.MemberResponseDto;
import com.example.shelfsync.Models.Entities.Member;
import com.example.shelfsync.Repositories.MemberRepository;
import com.example.shelfsync.Security.JwtService;
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

    public String loginValidation(LoginRequestDto loginRequestDto) throws Exception {
        Member member = memberRepository.findByMemberEmail(loginRequestDto.email()).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        if (!passwordEncoder.matches(loginRequestDto.password(),member.getPassword())) {
            throw new InvalidPasswordException("Invalid password!");
        }
        // 3. Generate token if credentials match
        String token = jwtService.generateToken(loginRequestDto.email());
        return token;
    }
}
