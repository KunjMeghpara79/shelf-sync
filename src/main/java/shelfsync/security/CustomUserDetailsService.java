package shelfsync.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shelfsync.models.entities.Admin;
import shelfsync.models.entities.Member;
import shelfsync.repositories.AdminRepository;
import shelfsync.repositories.MemberRepository;

import java.util.Optional;

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

        Optional<Admin> admin = adminRepository.findByAdminEmail(username);
        if (admin.isPresent()) {
            return User.builder()
                    .username(admin.get().getAdminEmail())
                    .password(admin.get().getPassword())
                    .roles("ADMIN")
                    .build();
        }
        /*
        Reason to  use optional is because both methods returns optional so if i do not use it then i have to throw exception
        and if i do it then if admin throws exception then member part won't be executed.
         */
        Optional<Member> member = memberRepository.findByMemberEmail(username);
        if (member.isPresent()) {
            return User.builder()
                    .username(member.get().getMemberEmail())
                    .password(member.get().getPassword())
                    .roles("MEMBER")
                    .build();
        }

        throw new UsernameNotFoundException("No system user found for: " + username);
    }

}