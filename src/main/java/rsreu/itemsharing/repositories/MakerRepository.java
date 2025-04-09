package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Maker;

public interface MakerRepository extends JpaRepository<Maker, Long> {
}