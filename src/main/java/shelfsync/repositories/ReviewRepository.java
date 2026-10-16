package shelfsync.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import shelfsync.models.entities.BookData;
import shelfsync.models.entities.Member;
import shelfsync.models.entities.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review findByBookDataAndMember(BookData bookData, Member member);
    List<Review> findByBookData(BookData bookData);
}
