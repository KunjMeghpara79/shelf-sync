package shelfsync.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import shelfsync.Models.Entities.Loan;

public interface LoanRepository extends JpaRepository<Loan,Integer> {
}
