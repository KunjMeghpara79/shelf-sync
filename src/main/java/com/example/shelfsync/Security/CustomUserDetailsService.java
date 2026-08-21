package com.example.shelfsync.Security;
import com.example.shelfsync.Exceptions.MemberNotFoundException;
import com.example.shelfsync.Models.Entities.Member;
import com.example.shelfsync.Repositories.MemberRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberEmail(username).orElseThrow(() -> new MemberNotFoundException("Member not found!"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getMemberEmail())
                .password(member.getPassword())
                .build();
    }
}