package shelfsync.models.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "book_data")
public class BookData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_data_id")
    private int bookDataId;

    @Column(unique = true,nullable = false,name = "book_name")
    private String bookName;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "author")
    private String authorName;


//    @Column(name = "available_quantity")
//    private int availableQuantity;

    @OneToMany(mappedBy = "bookData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Book> books;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinColumn(name = "book_data_id")
   private Set<Loan> loans;

    public BookData(String bookName, int totalQuantity){
        this.bookName = bookName;
        this.totalQuantity = totalQuantity;
    }
}
