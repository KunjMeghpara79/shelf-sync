package shelfsync.Models.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "available_quantity")
    private int availableQuantity;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Loan> loans = new HashSet<>();

    public Book(String bookName,int totalQuantity,int availableQuantity){
        this.bookName = bookName;
        this.availableQuantity=availableQuantity;
        this.totalQuantity = totalQuantity;
    }
}
