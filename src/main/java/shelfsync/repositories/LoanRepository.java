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
    List<Loan> findByLoanStatusInAndMember(List<LoanStatus> loanStatuses, Member member);
    List<Loan> findByLoanStatusIn(List<LoanStatus> statuses);

}
