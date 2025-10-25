package wisoft.tddstart.commerce;

import java.rmi.server.UID;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopperRepository extends JpaRepository<Shopper, Long> {
    Optional<Shopper> findById(UUID id);

    Optional<Shopper> findByEmail(String email);
}
