package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
