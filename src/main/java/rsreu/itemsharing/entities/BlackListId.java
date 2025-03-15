package rsreu.itemsharing.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class BlackListId implements Serializable {

    private Long blockedUser;
    private Long blockedByUser;

    public BlackListId() {}

    public BlackListId(Long blockedUser, Long blockedByUser) {
        this.blockedUser = blockedUser;
        this.blockedByUser = blockedByUser;
    }


    public Long getBlockedUser() {
        return blockedUser;
    }

    public void setBlockedUser(Long blockedUser) {
        this.blockedUser = blockedUser;
    }

    public Long getBlockedByUser() {
        return blockedByUser;
    }

    public void setBlockedByUser(Long blockedByUser) {
        this.blockedByUser = blockedByUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlackListId that = (BlackListId) o;
        return blockedUser.equals(that.blockedUser) && blockedByUser.equals(that.blockedByUser);
    }

    @Override
    public int hashCode() {
        return 31 * (blockedUser.hashCode() + blockedByUser.hashCode());
    }
}
