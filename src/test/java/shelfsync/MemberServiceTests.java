package shelfsync;

import lombok.With;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import shelfsync.enums.LoanStatus;
import shelfsync.models.dto.BookDataResponseDto;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.dto.SearchRequestDto;
import shelfsync.models.entities.Member;
import shelfsync.repositories.LoanRepository;
import shelfsync.repositories.MemberRepository;
import shelfsync.services.interfaces.MemberService;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class MemberServiceTests {

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private LoanRepository loanRepository;

    @Autowired
    private MemberService memberService;
    @Test
    @WithMockUser(username = "kunj@gmail.com",roles = "MEMBER")
    public void getAvailableBooksTest(){
        System.out.println(memberService.getAvailableBooks());
    }

    @Test
    @WithMockUser(username = "kunj@gmail.com",roles = "MEMBER")
    public void findBybookName(){
        List<BookDataResponseDto> bookDataResponseDto = memberService.findByBookName(new SearchRequestDto("great"));
        System.out.println(bookDataResponseDto);
    }
    @Test
    @WithMockUser(username = "kunj@gmail.com", roles = "MEMBER")
    void getLoansReportTest() {

        Member member = new Member();
        member.setMemberName("Kunj");
        member.setMemberEmail("kunj@gmail.com");

        when(memberRepository.findByMemberEmail("kunj@gmail.com"))
                .thenReturn(Optional.of(member));

        when(loanRepository.findByLoanStatusInAndMember(
                List.of(LoanStatus.DUE, LoanStatus.PENDING),
                member
        )).thenReturn(List.of());

        List<LoanResponseDto> result = memberService.getLoansReport();

        System.out.println(result);
    }

    @Test
    @WithMockUser(username = "kunj@gmail.com", roles = "MEMBER")
    public void getLoansHistoryTest(){
        Member member = new Member();
        member.setMemberName("Kunj");
        member.setMemberEmail("kunj@gmail.com");
        when(memberRepository.findByMemberEmail("kunj@gmail.com"))
                .thenReturn(Optional.of(member));
        when(loanRepository.findByLoanStatusInAndMember(List.of(LoanStatus.PAID),member)).thenReturn(List.of());

        System.out.println(memberService.getLoanHistory());
    }

    @Test
    @WithMockUser(username = "kunj@gmail.com",roles = "MEMBER")
    public void findByauthorName(){
        List<BookDataResponseDto> bookDataResponseDto = memberService.findByAuthorName(new SearchRequestDto("scott"));
        System.out.println(bookDataResponseDto);
    }

}
