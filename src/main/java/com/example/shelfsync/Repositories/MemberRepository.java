package com.example.shelfsync.Repositories;
import com.example.shelfsync.Models.Entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Integer> {
    public Optional<Member> findByMemberEmail(String email);
}
