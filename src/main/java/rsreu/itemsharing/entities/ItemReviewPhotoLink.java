package rsreu.itemsharing.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "item_review_photo_links")
public class ItemReviewPhotoLink {

    @EmbeddedId
    private ItemReviewPhotoLinkId id;

    @ManyToOne
    @MapsId("itemReviewId")
    @JoinColumn(name = "item_review_id", referencedColumnName = "item_review_id", insertable = false, updatable = false)
    private ItemReview itemReview;

    @ManyToOne
    @MapsId("photoId")
    @JoinColumn(name = "photo_id", referencedColumnName = "photo_id", insertable = false, updatable = false)
    private PhotoLink photoLink;

    public ItemReviewPhotoLink() {}

    public ItemReviewPhotoLink(ItemReviewPhotoLinkId id, ItemReview itemReview, PhotoLink photoLink) {
        this.id = id;
        this.itemReview = itemReview;
        this.photoLink = photoLink;
    }

    public ItemReviewPhotoLinkId getId() {
        return id;
    }

    public void setId(ItemReviewPhotoLinkId id) {
        this.id = id;
    }

    public ItemReview getItemReview() {
        return itemReview;
    }

    public void setItemReview(ItemReview itemReview) {
        this.itemReview = itemReview;
    }

    public PhotoLink getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(PhotoLink photoLink) {
        this.photoLink = photoLink;
    }
}
