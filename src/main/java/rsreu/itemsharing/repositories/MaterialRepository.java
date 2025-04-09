package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {
}