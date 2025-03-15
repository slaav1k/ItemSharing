package rsreu.itemsharing.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ItemPhotoLinkId implements Serializable {
    private String itemId;
    private Long photoId;

    public ItemPhotoLinkId() {}

    public ItemPhotoLinkId(String itemId, Long photoId) {
        this.itemId = itemId;
        this.photoId = photoId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

        ItemPhotoLinkId that = (ItemPhotoLinkId) o;

        if (!itemId.equals(that.itemId)) return false;
        return photoId.equals(that.photoId);
    }

    @Override
    public int hashCode() {
        int result = itemId.hashCode();
        result = 31 * result + photoId.hashCode();
        return result;
    }
}
