package rsreu.itemsharing.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "blacklist")
public class BlackList {

    @EmbeddedId
    private BlackListId id;

    @ManyToOne
    @MapsId("blockedUser")
    @JoinColumn(name = "blocked_user", nullable = false)
    private User blockedUserEntity;

    @ManyToOne
    @MapsId("blockedByUser")
    @JoinColumn(name = "blocked_by_user", nullable = false)
    private User blockedByUserEntity;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public BlackList() {}

    public BlackList(BlackListId id, User blockedUserEntity, User blockedByUserEntity, LocalDate date) {
        this.id = id;
        this.blockedUserEntity = blockedUserEntity;
        this.blockedByUserEntity = blockedByUserEntity;
        this.date = date;
    }

    public BlackListId getId() {
        return id;
    }

    public void setId(BlackListId id) {
        this.id = id;
    }

    public User getBlockedUserEntity() {
        return blockedUserEntity;
    }

    public void setBlockedUserEntity(User blockedUserEntity) {
        this.blockedUserEntity = blockedUserEntity;
    }

    public User getBlockedByUserEntity() {
        return blockedByUserEntity;
    }

    public void setBlockedByUserEntity(User blockedByUserEntity) {
        this.blockedByUserEntity = blockedByUserEntity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
