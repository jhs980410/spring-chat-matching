package domain.manager.repository;




import domain.manager.entity.TicketManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketManagerRepository extends JpaRepository<TicketManager, Long> {

    Optional<TicketManager> findByEmail(String email);
}
