package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Attribute;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
}
