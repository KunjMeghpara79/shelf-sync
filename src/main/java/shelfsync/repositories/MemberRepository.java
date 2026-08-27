package shelfsync.repositories;
import shelfsync.enums.MemberStatus;
import shelfsync.models.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Integer> {
    public Optional<Member> findByMemberEmail(String email);
    public boolean existsByMemberEmail(String email);
    public List<Member> findByFineGreaterThanEqualAndMemberStatus(int fine, MemberStatus memberStatus);
}
