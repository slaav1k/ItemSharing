package rsreu.itemsharing.entities;

import java.time.LocalDate;

public class BlackList {
    private Long blockedUser;
    private Long blockedByUser;
    private LocalDate date;

    public BlackList() {}

    public BlackList(Long blockedUser, Long blockedByUser, LocalDate date) {
        this.blockedUser = blockedUser;
        this.blockedByUser = blockedByUser;
        this.date = date;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
