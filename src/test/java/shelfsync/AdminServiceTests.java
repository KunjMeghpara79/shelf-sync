package shelfsync;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import shelfsync.models.dto.LoanResponseDto;
import shelfsync.repositories.AdminRepository;
import shelfsync.services.interfaces.AdminService;

import java.util.List;

@SpringBootTest
public class AdminServiceTests {

    @Mock
    private AdminRepository adminRepository;

    @Autowired
    private AdminService adminService;

    @Test
    @WithMockUser(username = "admin@niyantras.com",roles = "ADMIN")
    public void getLoansReportTest(){
        List<LoanResponseDto> loanResponseDtos = adminService.getLoansReport();
        System.out.println(loanResponseDtos);
    }

}
