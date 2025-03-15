package rsreu.itemsharing.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "review")
public class Review {

    @EmbeddedId
    private ReviewId id;

    @ManyToOne
    @MapsId("reviewed")
    @JoinColumn(name = "reviewed", nullable = false)
    private User reviewed;

    @ManyToOne
    @MapsId("reviewer")
    @JoinColumn(name = "reviewer", nullable = false)
    private User reviewer;

    @Column(name = "comment", nullable = false, length = 255)
    private String comment;

    @Column(name = "score", nullable = false)
    private int score;

    public Review() {}

    public Review(ReviewId id, User reviewed, User reviewer, String comment, int score) {
        this.id = id;
        this.reviewed = reviewed;
        this.reviewer = reviewer;
        this.comment = comment;
        this.score = score;
    }

    public ReviewId getId() {
        return id;
    }

    public void setId(ReviewId id) {
        this.id = id;
    }

    public User getReviewed() {
        return reviewed;
    }

    public void setReviewed(User reviewed) {
        this.reviewed = reviewed;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
