package shelfsync;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shelfsync.enums.MemberStatus;
import shelfsync.models.Observer.EmailObserver;
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
        EmailObserver emailObserver = new EmailObserver(emailService);

        member.getObservers().add(emailObserver);

        member.restrictMember();

        System.out.println("Member Status: " + member.getMemberStatus());
    }
}