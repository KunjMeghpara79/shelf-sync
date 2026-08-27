package shelfsync.Security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shelfsync.Exceptions.MemberNotFoundException;
import shelfsync.Models.Entities.Admin;
import shelfsync.Models.Entities.Member;
import shelfsync.Repositories.AdminRepository;
import shelfsync.Repositories.MemberRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    public CustomUserDetailsService(
            MemberRepository memberRepository,
            AdminRepository adminRepository) {

        this.memberRepository = memberRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var admin = adminRepository.findByAdminEmail(username);
        if (admin.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(admin.get().getAdminEmail())
                    .password(admin.get().getPassword())
                    .roles("ADMIN")
                    .build();
        }

        var member = memberRepository.findByMemberEmail(username);
        if (member.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(member.get().getMemberEmail())
                    .password(member.get().getPassword())
                    .roles("MEMBER")
                    .build();
        }

        throw new UsernameNotFoundException("No system user found for: " + username);
    }

}