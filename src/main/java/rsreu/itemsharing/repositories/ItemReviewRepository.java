package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.ItemReview;

public interface ItemReviewRepository extends JpaRepository<ItemReview, Long> {
}
