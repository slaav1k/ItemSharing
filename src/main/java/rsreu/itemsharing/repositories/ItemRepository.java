package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Category;
import rsreu.itemsharing.entities.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> {
    List<Item> findByCategory(Category category);
    List<Item> findAllByOrderByUpdatedAtDesc();
}
