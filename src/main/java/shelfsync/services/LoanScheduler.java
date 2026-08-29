package shelfsync.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shelfsync.enums.LoanStatus;
import shelfsync.models.entities.Loan;
import shelfsync.models.entities.Member;
import shelfsync.repositories.LoanRepository;
import shelfsync.repositories.MemberRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanScheduler {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;

    @Value("${loan.fine.fixedrate}")
    private int FIXED_FINE;

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
        }

        loanRepository.saveAll(loans);
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void addFines(){

        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        List<Loan> loans = loanRepository.findByLoanStatus(LoanStatus.DUE);

        for(Loan loan : loans){
            Member member = loan.getMember();
            member.setFine(member.getFine() + FIXED_FINE);
        }
    }
}