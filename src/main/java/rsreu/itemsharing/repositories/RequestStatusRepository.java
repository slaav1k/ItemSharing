package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.RequestStatus;

public interface RequestStatusRepository extends JpaRepository<RequestStatus, Long> {
}
