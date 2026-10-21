package shelfsync.repositories;

import shelfsync.enums.BookCategory;
import shelfsync.models.entities.BookData;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface BookDataRepository extends JpaRepository<BookData,Integer> {
    Optional<List<BookData>> findBybookNameContainingIgnoreCase(String bookName);
    Optional<List<BookData>> findByauthorNameContainingIgnoreCase(String authorName);
    List<BookData> findByBookCategory(BookCategory bookCategory);
    Optional<BookData> findByBookNameAndAuthorNameAndBookCategory(String bookName, String authorName, BookCategory bookCategory);

}
