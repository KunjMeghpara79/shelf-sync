package shelfsync.Repositories;

import shelfsync.Models.Entities.Book;
import shelfsync.Models.Entities.BookData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookDataRepository extends JpaRepository<BookData,Integer> {
    public BookData findBybookName(String bookName);
}
