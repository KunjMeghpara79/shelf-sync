package shelfsync.Models.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;

    @Column(unique = true,nullable = false,name = "book_name")
    private String bookName;

    @OneToOne
    @JoinColumn(name = "loan_id")
    private Loan loan = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_data_id", nullable = false)
    private BookData bookData;

}
