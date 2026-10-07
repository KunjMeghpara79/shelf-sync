package shelfsync;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shelfsync.enums.MemberStatus;
import shelfsync.models.entities.Member;
import shelfsync.services.interfaces.EmailService;

@SpringBootTest
class EmailTest {

    @Autowired
    private EmailService emailService;

    @Test
    void restrictMember_shouldChangeStatusAndSendEmail() {

        Member member = new Member();

        member.setMemberEmail("23dcs060@charusat.edu.in");
        member.setMemberStatus(MemberStatus.ACTIVE);

        member.restrictMember(emailService);

        System.out.println("Member Status: " + member.getMemberStatus());
    }
}