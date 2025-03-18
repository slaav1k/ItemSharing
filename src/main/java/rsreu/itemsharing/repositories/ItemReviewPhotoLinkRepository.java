package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.ItemReview;
import rsreu.itemsharing.entities.ItemReviewPhotoLink;

import java.util.List;

public interface ItemReviewPhotoLinkRepository extends JpaRepository<ItemReviewPhotoLink, Long> {
    List<ItemReviewPhotoLink> findByItemReview(ItemReview itemReview);
}
