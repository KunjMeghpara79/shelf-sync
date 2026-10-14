package shelfsync.repositories;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import shelfsync.models.entities.BookData;
import shelfsync.models.entities.Member;
import shelfsync.models.entities.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Review findByBookDataAndMember(BookData bookData, Member member);
}
