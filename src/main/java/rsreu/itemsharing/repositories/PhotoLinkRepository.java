package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.PhotoLink;

public interface PhotoLinkRepository extends JpaRepository<PhotoLink, Long> {
}
