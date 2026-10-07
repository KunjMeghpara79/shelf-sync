package shelfsync.models.Observer;

import shelfsync.models.entities.EmailDetails;
import shelfsync.models.entities.Member;
import shelfsync.services.interfaces.EmailService;

public class EmailObserver implements MemberObserver {

    private EmailService emailService;

    public EmailObserver(EmailService emailService) {
        this.emailService = emailService;
    }


    @Override
    public void memberRestricted(Member member) {
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
                member.getMemberEmail(),
                emailBody,
                "ShelfSync - Account Restricted",
                null
        );

        System.out.println(emailService.sendSimpleMail(emailDetails));
    }
}