package shelfsync.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import shelfsync.models.entities.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Integer> {
    public Optional<Admin> findByAdminEmail(String email);
}
