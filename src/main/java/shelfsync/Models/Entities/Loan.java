package shelfsync.Models.Entities;
import jakarta.persistence.*;
import lombok.*;
import shelfsync.Enums.LoanStatus;

import java.time.LocalDateTime;

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

    @Column(name = "start_time")
    private LocalDateTime issueDate = LocalDateTime.now();

    @Column(name = "end_time")
    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookData bookData = null;

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus = LoanStatus.PENDING;

    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book = null;
}
