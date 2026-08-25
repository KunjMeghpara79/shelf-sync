package shelfsync.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shelfsync.Enums.LoanStatus;
import shelfsync.Models.Entities.Loan;
import shelfsync.Models.Entities.Member;
import shelfsync.Repositories.LoanRepository;
import shelfsync.Repositories.MemberRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanScheduler {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final int FIXED_FINE = 5;
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