package shelfsync.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shelfsync.Enums.LoanStatus;
import shelfsync.Models.Entities.Loan;
import shelfsync.Repositories.LoanRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanScheduler {

    private final LoanRepository loanRepository;

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
}