package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemReview;

import java.util.List;

public interface ItemReviewRepository extends JpaRepository<ItemReview, Long> {
    List<ItemReview> findByItem(Item item);
}
