package rsreu.itemsharing.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
public class ReviewId implements Serializable {

    @Column(name = "reviewed")
    private Long reviewed;

    @Column(name = "reviewer")
    private Long reviewer;

    @Column(name = "date")
    private LocalDate date;

    public ReviewId() {}

    public ReviewId(Long reviewed, Long reviewer, LocalDate date) {
        this.reviewed = reviewed;
        this.reviewer = reviewer;
        this.date = date;
    }

    public Long getReviewed() {
        return reviewed;
    }

    public void setReviewed(Long reviewed) {
        this.reviewed = reviewed;
    }

    public Long getReviewer() {
        return reviewer;
    }

    public void setReviewer(Long reviewer) {
        this.reviewer = reviewer;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewId reviewId = (ReviewId) o;
        return reviewed.equals(reviewId.reviewed) && reviewer.equals(reviewId.reviewer) && date.equals(reviewId.date);
    }

    @Override
    public int hashCode() {
        return 31 * (reviewed.hashCode() + reviewer.hashCode() + date.hashCode());
    }
}

