package entity;

import java.time.LocalDateTime;

/**
 * the journal feature of the app.
 * written by user so store a user id (?), numerical rating/score,
 * string review, option to make public to store on recipe or remain private for user
 */
public class Review {
    private final int recipeId;
    private final int rating;           // 0-10 scale
    private final String author;
    private final String text;
    private final LocalDateTime lastReviewedAt;

    public Review(int recipeId, int rating,String author, String text, LocalDateTime lastReviewedAt) {
        this.recipeId = recipeId;
        this.rating = rating;
        this.author = author;
        this.text = text;
        this.lastReviewedAt = lastReviewedAt;
    }

    public int getRecipeId() { return recipeId; }
    public int getRating() { return rating; }
    public String getAuthor() { return author; }
    public String getText() { return text; }
    public LocalDateTime getLastReviewedAt() { return lastReviewedAt; }
}
