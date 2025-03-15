package rsreu.itemsharing.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "item_photo_links")
public class ItemPhotoLink {

    @EmbeddedId
    private ItemPhotoLinkId id;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", insertable = false, updatable = false)
    private Item item;

    @ManyToOne
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "photo_id", insertable = false, updatable = false)
    private PhotoLink photoLink;

    public ItemPhotoLink() {}

    public ItemPhotoLink(ItemPhotoLinkId id, Item item, PhotoLink photoLink) {
        this.id = id;
        this.item = item;
        this.photoLink = photoLink;
    }

    public ItemPhotoLinkId getId() {
        return id;
    }

    public void setId(ItemPhotoLinkId id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public PhotoLink getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(PhotoLink photoLink) {
        this.photoLink = photoLink;
    }
}
