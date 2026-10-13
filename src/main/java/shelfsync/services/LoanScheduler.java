package shelfsync.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shelfsync.enums.LoanStatus;
import shelfsync.models.entities.EmailDetails;
import shelfsync.models.entities.Loan;
import shelfsync.models.entities.Member;
import shelfsync.repositories.LoanRepository;
import shelfsync.services.interfaces.EmailService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanScheduler {

    private final LoanRepository loanRepository;

    @Value("${loan.fine.fixedrate}")
    private int FIXED_FINE;

    private final EmailService emailService;
    private String message = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">
            <h2 style="color: #d32f2f;">Book Return Reminder</h2>

            <p>Hello <strong>%s</strong>,</p>

            <p>
                This is a reminder that the book you borrowed from
                <strong>ShelfSync</strong> has reached its due date.
            </p>

            <p>
                <strong>Book Name:</strong> %s
            </p>

            <p>
                Please return the book as soon as possible to avoid
                additional overdue fines.
            </p>

            <p>Thank you for using ShelfSync!</p>

            <p>
                Regards,<br>
                <strong>Team ShelfSync</strong>
            </p>
        </body>
        </html>
        """;

    private void sendEmail(EmailDetails emailDetails){
        emailService.sendSimpleMail(emailDetails);
    }
    @Scheduled(fixedRate = 1000)
    public void checkOverdueLoans() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        List<Loan> loans =
                loanRepository.findByDueDateBeforeAndLoanStatus(
                        now,
                        LoanStatus.PENDING
                );
        for (Loan loan : loans) {
            loan.setLoanStatus(LoanStatus.DUE);
            String emailMessage = message.formatted(
                    loan.getMember().getMemberName(),
                    loan.getBook().getBookName()
            );

            EmailDetails emailDetails = new EmailDetails(
                    loan.getMember().getMemberEmail(),
                    emailMessage,
                    "Book Return Reminder",
                    null
            );
            sendEmail(emailDetails);
        }
        loanRepository.saveAll(loans);
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void addFines(){
        List<Loan> loans = loanRepository.findByLoanStatusIn(List.of(LoanStatus.DUE));
        for(Loan loan : loans){
            Member member = loan.getMember();
            loan.setFine(loan.getFine() + FIXED_FINE);
            member.setFine(member.getFine() + FIXED_FINE);
            log.info(String.valueOf(loan.getFine()));
        }
    }
}