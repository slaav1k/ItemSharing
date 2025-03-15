package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Review;
import rsreu.itemsharing.entities.ReviewId;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {
}
