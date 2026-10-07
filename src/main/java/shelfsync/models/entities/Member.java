package shelfsync.models.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import shelfsync.enums.MemberStatus;
import shelfsync.services.interfaces.EmailService;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private int memberId;

    @Column(name = "member_name")
    private String memberName;

    @Email
    @Column(name = "member_email", unique = true,nullable = false)
    private String memberEmail;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Loan> loans = new HashSet<>();

    @Column(name = "fine")
    private int fine = 0;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus = MemberStatus.ACTIVE;

    public void restrictMember(EmailService emailService) {

        this.setMemberStatus(MemberStatus.RESTRICTED);

        String emailBody = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 30px;">

                <div style="max-width: 600px; margin: auto; background-color: white;
                            padding: 30px; border-radius: 10px;">

                    <h2 style="color: #d32f2f;">
                        Library Account Restricted
                    </h2>

                    <p>Hello,</p>

                    <p>
                        Your ShelfSync library account has been
                        <strong>restricted</strong>.
                    </p>

                    <p>
                        You are currently not allowed to borrow any new books.
                    </p>

                    <div style="background-color: #ffebee; padding: 15px;
                                border-left: 5px solid #d32f2f; margin: 20px 0;">

                        <strong>Account Status:</strong> RESTRICTED

                    </div>

                    <p>
                        Please contact the library administrator for more
                        information.
                    </p>

                    <p>
                        Regards,<br>
                        <strong>ShelfSync Library</strong>
                    </p>

                </div>

            </body>
            </html>
            """;

        EmailDetails emailDetails = new EmailDetails(
                this.getMemberEmail(),
                emailBody,
                "ShelfSync - Account Restricted",
                null
        );

        System.out.println(emailService.sendSimpleMail(emailDetails));
    }

}
