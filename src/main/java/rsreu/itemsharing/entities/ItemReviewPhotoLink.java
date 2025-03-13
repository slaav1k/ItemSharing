package rsreu.itemsharing.entities;

public class ItemReviewPhotoLink {
    private Long itemReviewId;
    private Long photoId;

    public ItemReviewPhotoLink() {}

    public ItemReviewPhotoLink(Long itemReviewId, Long photoId) {
        this.itemReviewId = itemReviewId;
        this.photoId = photoId;
    }

    public Long getItemReviewId() {
        return itemReviewId;
    }

    public void setItemReviewId(Long itemReviewId) {
        this.itemReviewId = itemReviewId;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }
}
