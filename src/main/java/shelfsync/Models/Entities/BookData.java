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
public class BookData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_data_id")
    private int bookDataId;

    @Column(unique = true,nullable = false,name = "book_name")
    private String bookName;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "available_quantity")
    private int availableQuantity;


    public BookData(String bookName, int totalQuantity, int availableQuantity){
        this.bookName = bookName;
        this.availableQuantity=availableQuantity;
        this.totalQuantity = totalQuantity;
    }
}
