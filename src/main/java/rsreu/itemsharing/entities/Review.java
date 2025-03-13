package rsreu.itemsharing.entities;

import java.time.LocalDate;

public class Review {
    private Long reviewed;
    private String reviewer;
    private String comment;
    private int score;
    private LocalDate date;

    public Review() {}

    public Review(Long reviewed, String reviewer, String comment, int score, LocalDate date) {
        this.reviewed = reviewed;
        this.reviewer = reviewer;
        this.comment = comment;
        this.score = score;
        this.date = date;
    }

    public Long getReviewed() {
        return reviewed;
    }

    public void setReviewed(Long reviewed) {
        this.reviewed = reviewed;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
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
