package shelfsync.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import shelfsync.enums.LoanStatus;
import shelfsync.models.entities.Loan;
import shelfsync.models.entities.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan,Integer> {
    List<Loan> findByDueDateBeforeAndLoanStatus(
            LocalDateTime dateTime,
            LoanStatus loanStatus
    );
    List<Loan> findByLoanStatusAndMember(LoanStatus loanStatus, Member member);
    List<Loan> findByLoanStatus(LoanStatus loanStatus);
}
