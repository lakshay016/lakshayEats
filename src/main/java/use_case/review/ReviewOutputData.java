package use_case.review;

import java.time.LocalDateTime;

public class ReviewOutputData {
    private final String recipeId;
    private final int rating;
    private final String text;
    private final LocalDateTime lastReviewedAt;

    public ReviewOutputData(String recipeId, int rating, String text, LocalDateTime lastReviewedAt) {
        this.recipeId = recipeId;
        this.rating = rating;
        this.text = text;
        this.lastReviewedAt = lastReviewedAt;
    }

    public String getRecipeId() { return recipeId; }
    public int getRating() { return rating; }
    public String getText() { return text; }
    public LocalDateTime getLastReviewedAt() { return lastReviewedAt; }
}
