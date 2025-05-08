package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.ItemPhotoLink;
import rsreu.itemsharing.entities.ItemPhotoLinkId;
import rsreu.itemsharing.entities.PhotoLink;

import java.util.List;

public interface ItemPhotoLinkRepository extends JpaRepository<ItemPhotoLink, ItemPhotoLinkId> {
    List<ItemPhotoLink> findByItem(Item item);

    void deleteByItem_ItemId(String itemId);

    List<ItemPhotoLink> findByPhotoLink(PhotoLink photoLink);
}
