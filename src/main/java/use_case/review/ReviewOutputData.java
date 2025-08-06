package use_case.review;

import java.time.LocalDateTime;

public class ReviewOutputData {
    private final int recipeId;
    private final int rating;
    private final String author;
    private final String text;
    private final LocalDateTime lastReviewedAt;

    public ReviewOutputData(int recipeId, int rating, String author, String text, LocalDateTime lastReviewedAt) {
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
