package shelfsync.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import shelfsync.Models.Entities.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Integer> {
    public Optional<Admin> findByAdminEmail(String email);
}
