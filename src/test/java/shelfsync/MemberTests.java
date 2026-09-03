package shelfsync;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import shelfsync.models.dto.BookDataResponseDto;
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
    void getLoansHistoryTest() {
//        Member member = new Member();
//        member.setMemberName("Kunj");
//        member.setMemberEmail("kunj@gmail.com");
//        memberRepository.save(member);
        List<LoanResponseDto> loans = memberService.getLoanHistory();
        System.out.println(loans);
    }
    @Test
    @WithMockUser(username = "kunj@gmail.com", roles = "MEMBER")
    void getLoansReportTest() {
//        Member member = new Member();
//        member.setMemberName("Kunj");
//        member.setMemberEmail("kunj@gmail.com");
//        memberRepository.save(member);
        List<LoanResponseDto> loans = memberService.getLoansReport();
        System.out.println(loans);
    }

    @Test
    @WithMockUser(username = "kunj@gmail.com", roles = "MEMBER")
    public void getHistoryTest(){
      List<LoanResponseDto> loanResponseDtos = memberService.getLoanHistory();
        System.out.println(loanResponseDtos);
    }
    @Test
    @WithMockUser(username = "kunj@gmail.com", roles = "MEMBER")
    public void getAvailableBooks(){
      List<BookDataResponseDto> bookDataResponseDtos = memberService.getAvailableBooks();
        System.out.println(bookDataResponseDtos);
    }
    @Test
    @WithMockUser(username = "kunj@gmail.com", roles = "MEMBER")
    public void findByNameTest(){
      List<BookDataResponseDto> bookDataResponseDtos = memberService.findByBookName("great");
        System.out.println(bookDataResponseDtos);
    }
}
