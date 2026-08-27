package shelfsync.Models.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shelfsync.Enums.LoanStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "loan")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private int loanId;

    @Column(name = "issue_date")
    private LocalDateTime issueDate = LocalDateTime.now(ZoneId.of("UTC"));

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus = LoanStatus.PENDING;

    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book = null;

    @Column(name = "return_date")
    private LocalDateTime returnDate;


    @JoinColumn(name = "book_data_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BookData bookData;
}
