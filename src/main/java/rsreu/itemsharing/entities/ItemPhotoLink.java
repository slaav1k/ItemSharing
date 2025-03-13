package rsreu.itemsharing.entities;

public class ItemPhotoLink {
    private String itemId;
    private Long photoId;

    public ItemPhotoLink(String itemId, Long photoId) {
        this.itemId = itemId;
        this.photoId = photoId;
    }

    public ItemPhotoLink() {}

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
}
