package use_case.review;

public class ReviewInputData {
    private final String recipeId;
    private final int rating;
    private final String text;

    public ReviewInputData(String recipeId, int rating, String text) {
        this.recipeId = recipeId;
        this.rating = rating;
        this.text = text;
    }

    public String getRecipeId() { return recipeId; }
    public int getRating() { return rating; }
    public String getText() { return text; }
}
