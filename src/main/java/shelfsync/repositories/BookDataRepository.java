package shelfsync.repositories;

import shelfsync.models.entities.BookData;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface BookDataRepository extends JpaRepository<BookData,Integer> {
    public Optional<BookData> findBybookName(String bookName);
    public Optional<List<BookData>> findByauthorName(String authorName);
}
