package rsreu.itemsharing.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "item_review")
public class ItemReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_review_id", nullable = false)
    private Long itemReviewId;

    @ManyToOne
    @JoinColumn(name = "item", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "reviewer", nullable = false)
    private User reviewer;

    @Column(name = "comment", nullable = false, length = 255)
    private String comment;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public ItemReview() {}

    public ItemReview(Long itemReviewId, Item item, User reviewer, String comment, int score, LocalDate date) {
        this.itemReviewId = itemReviewId;
        this.item = item;
        this.reviewer = reviewer;
        this.comment = comment;
        this.score = score;
        this.date = date;
    }

    public Long getItemReviewId() {
        return itemReviewId;
    }

    public void setItemReviewId(Long itemReviewId) {
        this.itemReviewId = itemReviewId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
