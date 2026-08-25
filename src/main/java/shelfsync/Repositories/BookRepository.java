package shelfsync.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import shelfsync.Models.Entities.Book;

public interface BookRepository extends JpaRepository<Book,Integer> {

}
