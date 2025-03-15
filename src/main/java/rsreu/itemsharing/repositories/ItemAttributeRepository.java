package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.ItemAttribute;
import rsreu.itemsharing.entities.ItemAttributeId;

public interface ItemAttributeRepository extends JpaRepository<ItemAttribute, ItemAttributeId> {
}
