package rsreu.itemsharing.entities;

import java.time.LocalDate;

public class ItemReview {
    private Long itemReviewId;
    private String item;
    private Long reviewer;
    private String comment;
    private int score;
    private LocalDate date;

    public ItemReview() {}

    public ItemReview(Long itemReviewId, String item, Long reviewer, String comment, int score, LocalDate date) {
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

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Long getReviewer() {
        return reviewer;
    }

    public void setReviewer(Long reviewer) {
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
