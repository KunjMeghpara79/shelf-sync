package shelfsync.repositories;

import shelfsync.models.entities.BookData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookDataRepository extends JpaRepository<BookData,Integer> {
    public BookData findBybookName(String bookName);
}
