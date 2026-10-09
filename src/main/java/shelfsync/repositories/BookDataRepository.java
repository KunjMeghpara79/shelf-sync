package shelfsync.repositories;

import shelfsync.models.entities.BookData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookDataRepository extends JpaRepository<BookData,Integer> {
    Optional<List<BookData>> findBybookNameContainingIgnoreCase(String bookName);
    Optional<List<BookData>> findByauthorNameContainingIgnoreCase(String authorName);
}
