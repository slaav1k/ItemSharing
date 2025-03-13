package rsreu.itemsharing.entities;

public class PhotoLink {
    private Long PhotoId;
    private String url;

    public PhotoLink() {}

    public PhotoLink(Long PhotoId, String url) {
        this.PhotoId = PhotoId;
        this.url = url;
    }

    public Long getPhotoId() {
        return PhotoId;
    }

    public void setPhotoId(Long photoId) {
        PhotoId = photoId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
