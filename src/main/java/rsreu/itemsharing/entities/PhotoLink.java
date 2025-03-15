package rsreu.itemsharing.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "photo_links")
public class PhotoLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    @Column(name = "url")
    private String url;

    public PhotoLink() {}

    public PhotoLink(String url) {
        this.url = url;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
