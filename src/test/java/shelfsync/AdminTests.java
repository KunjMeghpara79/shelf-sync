package shelfsync;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import shelfsync.models.dto.BookDataRequestDto;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.models.entities.Member;
import shelfsync.repositories.MemberRepository;
import shelfsync.services.interfaces.AdminService;

import java.util.List;

@SpringBootTest
public class AdminTests {

    @Autowired
    private AdminService adminService;

    @Autowired
    private MemberRepository memberRepository;



    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    public void getLoansReportTest(){
        List<LoanResponseDto> loanResponseDtos = adminService.getLoansReport();
        System.out.println(loanResponseDtos);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    public void collectFineTest(){
        Member member = new Member();

        member.setMemberName("Kunj");
        member.setFine(10);
        member.setMemberEmail("kunj@gmail.com");

        memberRepository.save(member);
        adminService.payFine(1,10);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com",roles = "ADMIN")
    public void addBookTest(){
        BookDataRequestDto bookDataRequestDto = new BookDataRequestDto("48 laws of power","Robert green",3);
        adminService.addBook(bookDataRequestDto);
    }
}
