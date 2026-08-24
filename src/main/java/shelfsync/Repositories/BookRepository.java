package shelfsync.Repositories;

import shelfsync.Models.Entities.BookData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookData,Integer> {

}
