package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemAttribute;
import rsreu.itemsharing.entities.ItemAttributeId;

import java.util.List;

public interface ItemAttributeRepository extends JpaRepository<ItemAttribute, ItemAttributeId> {
    List<ItemAttribute> findById_Item(String item);

}
