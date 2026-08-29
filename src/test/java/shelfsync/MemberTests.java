package shelfsync;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.entities.Loan;
import shelfsync.models.entities.Member;
import shelfsync.repositories.LoanRepository;
import shelfsync.repositories.MemberRepository;
import shelfsync.services.interfaces.MemberService;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class MemberTests {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @WithMockUser(username = "kunj@gmail.com", roles = "MEMBER")
    void getLoansTest() {

        Member member = new Member();

        member.setMemberName("Kunj");
        member.setMemberEmail("kunj@gmail.com");

        memberRepository.save(member);

        List<LoanResponseDto> loans = memberService.getLoanHistory();

        System.out.println(loans);
    }
}
