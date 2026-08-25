package shelfsync.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import shelfsync.Enums.LoanStatus;
import shelfsync.Models.Entities.Loan;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan,Integer> {
    List<Loan> findByDueDateBeforeAndLoanStatus(
            LocalDateTime dateTime,
            LoanStatus loanStatus
    );
}
