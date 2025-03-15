package rsreu.itemsharing.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ItemReviewPhotoLinkId implements Serializable {
    private Long itemReviewId;
    private Long photoId;

    public ItemReviewPhotoLinkId() {}

    public ItemReviewPhotoLinkId(Long itemReviewId, Long photoId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemReviewPhotoLinkId that = (ItemReviewPhotoLinkId) o;

        if (!itemReviewId.equals(that.itemReviewId)) return false;
        return photoId.equals(that.photoId);
    }

    @Override
    public int hashCode() {
        int result = itemReviewId.hashCode();
        result = 31 * result + photoId.hashCode();
        return result;
    }
}
